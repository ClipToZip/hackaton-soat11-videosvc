package br.com.soat11.videosvc.presentation.controller;

import br.com.soat11.videosvc.application.dto.VideoStatusDTO;
import br.com.soat11.videosvc.application.service.VideoService;
import br.com.soat11.videosvc.core.domain.Video;
import br.com.soat11.videosvc.core.domain.VideoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {
    @Mock
    private VideoService videoService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private VideoController videoController;

    private UUID userId;
    private UUID videoId;

    @BeforeEach
    void setUp () {
        userId = UUID.randomUUID();
        videoId = UUID.randomUUID();
    }

    @Test
    void upload_DeveRetornarAccepted_QuandoTokenValido () throws Exception {
        String token = "Bearer valid.token";
        String title = "Video Teste";
        String description = "Descricao";
        Video video = mock(Video.class);
        when(videoService.validarToken(anyString())).thenReturn(true);
        when(videoService.iniciarUpload(any(), any(), anyString(), anyString())).thenReturn(video);
        when(video.getVideoId()).thenReturn(videoId);
        when(video.getStatus()).thenReturn(br.com.soat11.videosvc.core.domain.VideoStatus.PROCESSING.ordinal());
        // Simula JwtDecoder.extractSubWithoutValidation
        try (var mocked = Mockito.mockStatic(br.com.soat11.videosvc.utils.JwtDecoder.class)) {
            mocked.when(() -> br.com.soat11.videosvc.utils.JwtDecoder.extractSubWithoutValidation(token)).thenReturn(userId.toString());
            ResponseEntity<?> response = videoController.upload(token, title, description, multipartFile);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Test
    void upload_DeveRetornarUnauthorized_QuandoTokenInvalido () throws Exception {
        String token = "Bearer invalid.token";
        when(videoService.validarToken(anyString())).thenReturn(false);
        // Simula JwtDecoder.extractSubWithoutValidation
        try (var mocked = Mockito.mockStatic(br.com.soat11.videosvc.utils.JwtDecoder.class)) {
            mocked.when(() -> br.com.soat11.videosvc.utils.JwtDecoder.extractSubWithoutValidation(token)).thenReturn(userId.toString());
            ResponseEntity<?> response = videoController.upload(token, "t", "d", multipartFile);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Token inválido", response.getBody());
        }
    }

    @Test
    void listar_DeveRetornarOk_QuandoExistemVideos () {
        String token = "Bearer valid.token";
        List<VideoStatusDTO> videos = List.of(mock(VideoStatusDTO.class));
        when(videoService.listarVideosPorUsuario(userId)).thenReturn(videos);
        when(videoService.validarToken(anyString())).thenReturn(true);
        // Simula JwtDecoder.extractSubWithoutValidation
        try (var mocked = Mockito.mockStatic(br.com.soat11.videosvc.utils.JwtDecoder.class)) {
            mocked.when(() -> br.com.soat11.videosvc.utils.JwtDecoder.extractSubWithoutValidation(token)).thenReturn(userId.toString());
            ResponseEntity<List<VideoStatusDTO>> response = videoController.listar(userId, token);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(videos, response.getBody());
        }
    }

    @Test
    void listar_DeveRetornarNoContent_QuandoNaoExistemVideos () {
        String token = "Bearer valid.token";
        when(videoService.listarVideosPorUsuario(userId)).thenReturn(Collections.emptyList());
        when(videoService.validarToken(anyString())).thenReturn(true);
        // Simula JwtDecoder.extractSubWithoutValidation
        try (var mocked = Mockito.mockStatic(br.com.soat11.videosvc.utils.JwtDecoder.class)) {
            mocked.when(() -> br.com.soat11.videosvc.utils.JwtDecoder.extractSubWithoutValidation(token)).thenReturn(userId.toString());
            ResponseEntity<List<VideoStatusDTO>> response = videoController.listar(userId, token);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Test
    void downloadZip_DeveRetornarFoundComLocation () {
        String token = "Bearer valid.token";
        String url = "http://example.com/download.zip";
        when(videoService.obterLinkDownloadZip(videoId)).thenReturn(url);
        when(videoService.validarToken(anyString())).thenReturn(true);
        // Simula JwtDecoder.extractSubWithoutValidation
        try (var mocked = Mockito.mockStatic(br.com.soat11.videosvc.utils.JwtDecoder.class)) {
            mocked.when(() -> br.com.soat11.videosvc.utils.JwtDecoder.extractSubWithoutValidation(token)).thenReturn(userId.toString());
            ResponseEntity<Void> response = videoController.downloadZip(videoId, token);
            assertEquals(HttpStatus.FOUND, response.getStatusCode());
            assertEquals(URI.create(url), response.getHeaders().getLocation());
        }
    }
}