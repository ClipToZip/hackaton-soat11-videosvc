package br.com.soat11.videosvc.application.service;

import br.com.soat11.videosvc.application.dto.VideoEventDTO;
import br.com.soat11.videosvc.application.dto.VideoStatusDTO;
import br.com.soat11.videosvc.core.domain.Video;
import br.com.soat11.videosvc.core.ports.VideoStoragePort;
import br.com.soat11.videosvc.infra.messaging.SqsProducer;
import br.com.soat11.videosvc.infra.persistence.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoStoragePort storagePort;
    private final SqsProducer sqsProducer; // Seu componente de Kafka

    @Value("${external.api.url}")
    private String externalApiUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public VideoService (VideoRepository videoRepository,
                         VideoStoragePort storagePort,
                         SqsProducer sqsProducer) {
        this.videoRepository = videoRepository;
        this.storagePort = storagePort;
        this.sqsProducer = sqsProducer;
    }

    public Video iniciarUpload (MultipartFile file, UUID userId, String titulo, String descricao) {
        // 1. Salva o registro inicial (Rápido)
        Video video = Video.builder()
                .userId(userId)
                .titulo(titulo)
                .descricao(descricao)
                .status(1) // 1 - RECEBIDO
                .build();

        Video salvo = videoRepository.save(video);

        // 2. Dispara o processo pesado em background
        uploadENotificarSqs(file, salvo.getVideoId());

        return salvo;
    }

    @Async
    public void uploadENotificarSqs (MultipartFile file, UUID videoId) {
        try {
            // 3. Faz o upload para o S3 (o tal processo de 10 segundos)
            String finalFileName = storagePort.store(file, videoId.toString());

            // 4. Atualiza o banco para UPLOADED
            Video video = videoRepository.findById(videoId).orElseThrow();
            video.setVideoName(finalFileName);
            //video.setStatus(2);
            videoRepository.save(video);

            // 5. NOTIFICA O SQS (O pulo do gato)
            // Enviamos um DTO ou o próprio objeto para a fila de processamento
            sqsProducer.sendMessage(new VideoEventDTO(video.getVideoId(), video.getVideoName()));

            System.out.println("DEBUG: Upload concluído e mensagem enviada ao SQS: " + finalFileName);

        } catch (Exception e) {
            videoRepository.findById(videoId).ifPresent(v -> {
                v.setStatus(99); // Erro
                videoRepository.save(v);
            });
            System.err.println("ERRO no fluxo assíncrono: " + e.getMessage());
        }
    }

    public List<VideoStatusDTO> listarVideosPorUsuario(UUID userId) {
        return videoRepository.findByUserIdOrderByVideoUpDateDesc(userId)
                .stream()
                .map(VideoStatusDTO::fromEntity)
                .toList();
    }

    public String obterLinkDownloadZip(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado"));

        if (video.getZipName() == null) {
            throw new RuntimeException("O processamento do ZIP ainda não foi concluído.");
        }

        return storagePort.generateDownloadUrl(video.getZipName());
    }

    public boolean validarToken (String token) {
        try {
            // Monta o JSON com o atributo 'token'
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("token", token);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body);
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    externalApiUrl, entity, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao validar token: " + e.getMessage());
            return false;
        }
    }
}

