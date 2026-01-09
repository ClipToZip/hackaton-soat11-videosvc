package br.com.soat11.videosvc.presentation.controller;

import br.com.soat11.videosvc.application.service.VideoService;
import br.com.soat11.videosvc.core.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        Video video = videoService.uploadVideo(userId, title, description, file);

        return ResponseEntity.ok(new VideoResponseDTO(
                video.getVideoId(),
                video.getDescricao(),
                "PROCESSANDO"
        ));
    }
}

record VideoResponseDTO(UUID id, String descricao, String status) {}