package br.com.soat11.videosvc.presentation.controller;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoResponseDTOTest {
    @Test
    void constructorAndAccessors_ShouldWorkCorrectly() {
        UUID id = UUID.randomUUID();
        String descricao = "Mensagem de sucesso";
        String status = "PROCESSING";

        VideoResponseDTO dto = new VideoResponseDTO(id, descricao, status);

        assertEquals(id, dto.id());
        assertEquals(descricao, dto.descricao());
        assertEquals(status, dto.status());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForSameValues() {
        UUID id = UUID.randomUUID();
        VideoResponseDTO dto1 = new VideoResponseDTO(id, "desc", "OK");
        VideoResponseDTO dto2 = new VideoResponseDTO(id, "desc", "OK");
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        UUID id = UUID.randomUUID();
        VideoResponseDTO dto = new VideoResponseDTO(id, "desc", "OK");
        String str = dto.toString();
    }
}