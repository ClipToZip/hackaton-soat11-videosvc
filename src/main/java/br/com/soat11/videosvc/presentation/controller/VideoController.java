package br.com.soat11.videosvc.presentation.controller;

import br.com.soat11.videosvc.application.dto.VideoStatusDTO;
import br.com.soat11.videosvc.application.service.VideoService;
import br.com.soat11.videosvc.core.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<?> upload(
            @RequestHeader("Authorization") String auth,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile file) {

        // Extração simples do ID (ajuste conforme seu padrão de Token)
        UUID userId = UUID.fromString(auth.replace("Bearer ", ""));

        Video video = videoService.iniciarUpload(file,userId, title, description);

        return ResponseEntity.accepted().body(new VideoResponseDTO(
                video.getVideoId(),
                "Upload iniciado com sucesso. Acompanhe o status pelo ID.",
                video.getStatus().toString()
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VideoStatusDTO>> listar(@PathVariable UUID userId) {
        List<VideoStatusDTO> videos = videoService.listarVideosPorUsuario(userId);

        if (videos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{videoId}/download-zip")
    public ResponseEntity<Void> downloadZip(@PathVariable UUID videoId) {
        String downloadUrl = videoService.obterLinkDownloadZip(videoId);

        // Retorna um redirecionamento 302 para a URL da AWS
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(downloadUrl))
                .build();
    }
}

record VideoResponseDTO(UUID id, String descricao, String status) {}