package br.com.soat11.videosvc.infra.messaging;

import br.com.soat11.videosvc.application.dto.VideoEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Slf4j
public class SqsProducer {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.endpoint}")
    private String sqsEndpoint;

    public SqsProducer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(VideoEventDTO videoEventDTO) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(videoEventDTO);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(sqsEndpoint)
                    .messageBody(jsonMessage)
                    .build());
            log.info("Message sent to SQS: {}", videoEventDTO.getVideoId());
        } catch (Exception e) {
            log.error("Error sending message to SQS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}
