package br.com.soat11.videosvc.application.service;

import br.com.soat11.videosvc.application.dto.VideoEventDTO;
import br.com.soat11.videosvc.application.dto.VideoStatusDTO;
import br.com.soat11.videosvc.core.domain.Video;
import br.com.soat11.videosvc.core.ports.VideoStoragePort;
import br.com.soat11.videosvc.infra.messaging.SqsProducer;
import br.com.soat11.videosvc.infra.persistence.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VideoServiceTest {
    @Mock
    private VideoRepository videoRepository;
    @Mock
    private VideoStoragePort storagePort;
    @Mock
    private SqsProducer sqsProducer;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        videoService = new VideoService(videoRepository, storagePort, sqsProducer);
        // injeta manualmente o RestTemplate mockado
        try {
            java.lang.reflect.Field field = VideoService.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(videoService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // injeta manualmente a URL externa
        try {
            java.lang.reflect.Field field = VideoService.class.getDeclaredField("externalApiUrl");
            field.setAccessible(true);
            field.set(videoService, "http://fake-url");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void iniciarUpload_DeveSalvarVideoEChamarUploadENotificarSqs() {
        UUID userId = UUID.randomUUID();
        Video video = Video.builder().userId(userId).titulo("t").descricao("d").status(1).build();
        Video salvo = Video.builder().videoId(UUID.randomUUID()).userId(userId).titulo("t").descricao("d").status(1).build();
        when(videoRepository.save(any(Video.class))).thenReturn(salvo);
        VideoService spyService = Mockito.spy(new VideoService(videoRepository, storagePort, sqsProducer));
        // injeta dependências no spy
        try {
            java.lang.reflect.Field field = VideoService.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(spyService, restTemplate);
            field = VideoService.class.getDeclaredField("externalApiUrl");
            field.setAccessible(true);
            field.set(spyService, "http://fake-url");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        doNothing().when(spyService).uploadENotificarSqs(any(), any());

        Video result = spyService.iniciarUpload(multipartFile, userId, "t", "d");
        assertEquals(salvo, result);
        verify(videoRepository, times(1)).save(any(Video.class));
        verify(spyService, times(1)).uploadENotificarSqs(any(), any());
    }

    @Test
    void uploadENotificarSqs_DeveArmazenarAtualizarEEnviarMensagem() {
        UUID videoId = UUID.randomUUID();
        Video video = Video.builder().videoId(videoId).build();
        when(storagePort.store(any(), anyString())).thenReturn("file.mp4");
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(video);
        doNothing().when(sqsProducer).sendMessage(any(VideoEventDTO.class));

        videoService.uploadENotificarSqs(multipartFile, videoId);
        verify(storagePort).store(multipartFile, videoId.toString());
        verify(videoRepository, times(1)).save(any(Video.class));
        verify(sqsProducer).sendMessage(any(VideoEventDTO.class));
    }

    @Test
    void uploadENotificarSqs_DeveAtualizarStatusSeDerErro() {
        UUID videoId = UUID.randomUUID();
        Video video = Video.builder().videoId(videoId).build();
        when(storagePort.store(any(), anyString())).thenThrow(new RuntimeException("erro"));
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        videoService.uploadENotificarSqs(multipartFile, videoId);
        verify(videoRepository).save(argThat(v -> v.getStatus() != null && v.getStatus() == 99));
    }

    @Test
    void listarVideosPorUsuario_DeveRetornarListaDTO() {
        UUID userId = UUID.randomUUID();
        Video v = Video.builder().videoId(UUID.randomUUID()).userId(userId).build();
        when(videoRepository.findByUserIdOrderByVideoUpDateDesc(userId)).thenReturn(List.of(v));
        List<VideoStatusDTO> result = videoService.listarVideosPorUsuario(userId);
        assertEquals(1, result.size());
        assertEquals(v.getVideoId(), result.get(0).videoId());
    }

    @Test
    void obterLinkDownloadZip_DeveRetornarUrlSeZipNameExiste() {
        UUID videoId = UUID.randomUUID();
        Video v = Video.builder().videoId(videoId).zipName("file.zip").build();
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(v));
        when(storagePort.generateDownloadUrl("file.zip")).thenReturn("url");
        String url = videoService.obterLinkDownloadZip(videoId);
        assertEquals("url", url);
    }

    @Test
    void obterLinkDownloadZip_DeveLancarExcecaoSeVideoNaoExiste() {
        UUID videoId = UUID.randomUUID();
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> videoService.obterLinkDownloadZip(videoId));
    }

    @Test
    void obterLinkDownloadZip_DeveLancarExcecaoSeZipNameNulo() {
        UUID videoId = UUID.randomUUID();
        Video v = Video.builder().videoId(videoId).zipName(null).build();
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(v));
        assertThrows(RuntimeException.class, () -> videoService.obterLinkDownloadZip(videoId));
    }

    @Test
    void validarToken_DeveRetornarTrueParaStatus2xx() {
        String token = "abc";
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(response);
        assertTrue(videoService.validarToken(token));
    }

    @Test
    void validarToken_DeveRetornarFalseParaExcecao() {
        String token = "abc";
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenThrow(new RuntimeException("erro"));
        assertFalse(videoService.validarToken(token));
    }
}
