package br.com.soat11.videosvc.application.service;

import br.com.soat11.videosvc.core.domain.Video;
import br.com.soat11.videosvc.core.ports.VideoStoragePort;
import br.com.soat11.videosvc.infra.messaging.KafkaProducerImpl;
import br.com.soat11.videosvc.infra.persistence.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository repository;
    private final VideoStoragePort storagePort;
    private final KafkaProducerImpl kafkaProducer;

    @Transactional
    public Video uploadVideo(UUID userId, String titulo, String descricao, MultipartFile file) {
        System.out.println("DEBUG: Iniciando upload para o arquivo: " + file.getOriginalFilename());
        // 1. Persistência inicial
        Video video = Video.builder()
                .userId(userId)
                .titulo(titulo)
                .descricao(descricao)
                .status(1) // RECEIVED
                .build();

        video = repository.save(video);

        // 2. Armazenamento (Porta)
        String fileName = video.getVideoId() + "_" + file.getOriginalFilename();
        String path = storagePort.store(file, fileName);

        // 3. Atualização
        video.setVideoPath(path);
        video.setStatus(2); // UPLOADED
        repository.save(video);

        // 4. Mensageria
        kafkaProducer.sendVideoEvent(video);

        return video;
    }
}
