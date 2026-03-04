package br.com.soat11.videosvc.infra.messaging;

import br.com.soat11.videosvc.application.dto.VideoEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SqsProducerTest {
    @Mock
    private SqsClient sqsClient;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private SqsProducer sqsProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sqsProducer = new SqsProducer(sqsClient, objectMapper);
        // Seta o endpoint manualmente já que @Value não é processado em teste unitário
        try {
            java.lang.reflect.Field field = SqsProducer.class.getDeclaredField("sqsEndpoint");
            field.setAccessible(true);
            field.set(sqsProducer, "https://fake-queue-url");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sendMessage_DeveEnviarMensagemComSucesso() throws Exception {
        VideoEventDTO dto = mock(VideoEventDTO.class);
        when(objectMapper.writeValueAsString(dto)).thenReturn("json");
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(null);

        sqsProducer.sendMessage(dto);

        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient, times(1)).sendMessage(captor.capture());
        SendMessageRequest req = captor.getValue();
        assertEquals("https://fake-queue-url", req.queueUrl());
        assertEquals("json", req.messageBody());
    }

    @Test
    void sendMessage_DeveLancarExcecao_SeFalharSerializacao() throws Exception {
        VideoEventDTO dto = mock(VideoEventDTO.class);
        when(objectMapper.writeValueAsString(dto)).thenThrow(new RuntimeException("erro serializacao"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> sqsProducer.sendMessage(dto));
        assertTrue(ex.getMessage().contains("Failed to send message to SQS"));
    }
}