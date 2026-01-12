package br.com.soat11.videosvc.infra.messaging;

import br.com.soat11.videosvc.core.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducerImpl {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendVideoEvent(Video video) {
        // Enviamos um mapa ou DTO para o tópico
        var event = new VideoEventDTO(video.getVideoId(), video.getUserId(), video.getVideoName());
        kafkaTemplate.send("video-processing-topic", event);
    }
}

// DTO Auxiliar para o evento
record VideoEventDTO(java.util.UUID videoId, java.util.UUID userId, String path) {}
