package br.com.soat11.videosvc.core.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoTest {
    @Test
    void builderAndGetters_ShouldWorkCorrectly() {
        UUID videoId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadados = new HashMap<>();
        metadados.put("key", "value");

        Video video = Video.builder()
                .videoId(videoId)
                .userId(userId)
                .videoUpDate(now)
                .status(2)
                .videoName("video.mp4")
                .zipName("video.zip")
                .descricao("desc")
                .titulo("titulo")
                .metadados(metadados)
                .build();

        assertEquals(videoId, video.getVideoId());
        assertEquals(userId, video.getUserId());
        assertEquals(now, video.getVideoUpDate());
        assertEquals(2, video.getStatus());
        assertEquals("video.mp4", video.getVideoName());
        assertEquals("video.zip", video.getZipName());
        assertEquals("desc", video.getDescricao());
        assertEquals("titulo", video.getTitulo());
        assertEquals(metadados, video.getMetadados());
    }

    @Test
    void settersAndNoArgsConstructor_ShouldWork() {
        Video video = new Video();
        UUID videoId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadados = new HashMap<>();
        metadados.put("foo", 123);

        video.setVideoId(videoId);
        video.setUserId(userId);
        video.setVideoUpDate(now);
        video.setStatus(1);
        video.setVideoName("a.mp4");
        video.setZipName("a.zip");
        video.setDescricao("d");
        video.setTitulo("t");
        video.setMetadados(metadados);

        assertEquals(videoId, video.getVideoId());
        assertEquals(userId, video.getUserId());
        assertEquals(now, video.getVideoUpDate());
        assertEquals(1, video.getStatus());
        assertEquals("a.mp4", video.getVideoName());
        assertEquals("a.zip", video.getZipName());
        assertEquals("d", video.getDescricao());
        assertEquals("t", video.getTitulo());
        assertEquals(metadados, video.getMetadados());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForSameValues() {
        UUID videoId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Video v1 = Video.builder().videoId(videoId).userId(userId).videoUpDate(now).build();
        Video v2 = Video.builder().videoId(videoId).userId(userId).videoUpDate(now).build();
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        UUID videoId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Video video = Video.builder()
                .videoId(videoId)
                .userId(userId)
                .videoUpDate(now)
                .status(1)
                .videoName("file.mp4")
                .zipName("file.zip")
                .descricao("desc")
                .titulo("titulo")
                .build();
        String str = video.toString();
        assertTrue(str.contains(videoId.toString()));
        assertTrue(str.contains(userId.toString()));
        assertTrue(str.contains("file.mp4"));
        assertTrue(str.contains("file.zip"));
        assertTrue(str.contains("desc"));
        assertTrue(str.contains("titulo"));
    }
}