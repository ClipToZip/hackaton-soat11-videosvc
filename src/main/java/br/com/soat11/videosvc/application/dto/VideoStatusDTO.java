package br.com.soat11.videosvc.application.dto;

import br.com.soat11.videosvc.core.domain.Video;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoStatusDTO(
        UUID videoId,
        String titulo,
        String videoName,
        Integer status,
        String statusDescricao,
        LocalDateTime dataUpload
) {
    public static VideoStatusDTO fromEntity(Video video) {
        String descricao = switch (video.getStatus()) {
            case 1 -> "Recebido / Upload em processamento";
            case 2 -> "Upload concluído / Pronto para processar";
            case 3 -> "Processamento finalizado";
            case 99 -> "Erro no processamento";
            default -> "Status desconhecido";
        };

        return new VideoStatusDTO(
                video.getVideoId(),
                video.getTitulo(),
                video.getVideoName(),
                video.getStatus(),
                descricao,
                video.getVideoUpDate()
        );
    }
}