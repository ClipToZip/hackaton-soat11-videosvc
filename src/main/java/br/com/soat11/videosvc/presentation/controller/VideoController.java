package br.com.soat11.videosvc.presentation.controller;

import br.com.soat11.videosvc.application.dto.VideoStatusDTO;
import br.com.soat11.videosvc.application.service.VideoService;
import br.com.soat11.videosvc.core.domain.Video;
import br.com.soat11.videosvc.utils.JwtDecoder;
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
            @RequestParam UUID userId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam MultipartFile file) throws Exception {

        // Valida o token antes de qualquer ação
        boolean tokenValido = videoService.validarToken(auth);
        if (!tokenValido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

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

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(downloadUrl))
                .build();
    }
}

record VideoResponseDTO(UUID id, String descricao, String status) {}