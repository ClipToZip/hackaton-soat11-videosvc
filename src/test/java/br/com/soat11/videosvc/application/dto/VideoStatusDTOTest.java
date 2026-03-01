package br.com.soat11.videosvc.application.dto;

import br.com.soat11.videosvc.core.domain.Video;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoStatusDTOTest {
    @Test
    void fromEntity_DeveMapearCorretamenteTodosOsCampos() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2026, 3, 1, 12, 0);
        Video video = Video.builder()
                .videoId(id)
                .titulo("Titulo")
                .videoName("video.mp4")
                .status(2)
                .videoUpDate(now)
                .build();
        VideoStatusDTO dto = VideoStatusDTO.fromEntity(video);
        assertEquals(id, dto.videoId());
        assertEquals("Titulo", dto.titulo());
        assertEquals("video.mp4", dto.videoName());
        assertEquals(2, dto.status());
        assertEquals("Upload concluído / Pronto para processar", dto.statusDescricao());
        assertEquals(now, dto.dataUpload());
    }

    @Test
    void fromEntity_DeveRetornarStatusDesconhecidoParaStatusNulo() {
        Video video = Video.builder()
                .videoId(UUID.randomUUID())
                .titulo("T")
                .videoName("v.mp4")
                .status(null)
                .videoUpDate(LocalDateTime.now())
                .build();
        VideoStatusDTO dto = VideoStatusDTO.fromEntity(video);
        assertEquals("Status desconhecido", dto.statusDescricao());
        assertNull(dto.status());
    }

    @Test
    void fromEntity_DeveRetornarDescricaoCorretaParaTodosOsStatus() {
        int[] status = {1, 2, 3, 99, -5};
        String[] descricoes = {
                "Recebido / Upload em processamento",
                "Upload concluído / Pronto para processar",
                "Processamento finalizado",
                "Erro no processamento",
                "Status desconhecido"
        };
        for (int i = 0; i < status.length; i++) {
            Video video = Video.builder()
                    .videoId(UUID.randomUUID())
                    .titulo("T")
                    .videoName("v.mp4")
                    .status(status[i])
                    .videoUpDate(LocalDateTime.now())
                    .build();
            VideoStatusDTO dto = VideoStatusDTO.fromEntity(video);
            assertEquals(descricoes[i], dto.statusDescricao());
        }
    }
}

