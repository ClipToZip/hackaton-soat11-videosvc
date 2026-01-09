package br.com.soat11.videosvc.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- OBRIGATÓRIO PARA O KAFKA
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor // O Jackson precisa desse construtor vazio
public class VideoEventDTO {
    private UUID videoId;
    private UUID userId;
    private String videoPath;
}