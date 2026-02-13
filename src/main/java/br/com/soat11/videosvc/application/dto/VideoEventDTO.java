package br.com.soat11.videosvc.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- OBRIGATÓRIO PARA O KAFKA
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor // O Jackson precisa desse construtor vazio
public class VideoEventDTO {
    @JsonProperty("video_id")
    private UUID videoId;

    @JsonProperty("path")
    private String path;
}