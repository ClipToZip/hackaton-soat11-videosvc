package br.com.soat11.videosvc.application.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoEventDTOTest {
    @Test
    void constructorAndGetters_ShouldWork() {
        UUID id = UUID.randomUUID();
        String name = "video.mp4";
        VideoEventDTO dto = new VideoEventDTO(id, name);
        assertEquals(id, dto.getVideoId());
        assertEquals(name, dto.getPath());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForSameValues() {
        UUID id = UUID.randomUUID();
        String name = "file.mp4";
        VideoEventDTO dto1 = new VideoEventDTO(id, name);
        VideoEventDTO dto2 = new VideoEventDTO(id, name);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        UUID id = UUID.randomUUID();
        String name = "abc.mp4";
        VideoEventDTO dto = new VideoEventDTO(id, name);
        String str = dto.toString();
        assertTrue(str.contains(id.toString()));
        assertTrue(str.contains(name));
    }
}
