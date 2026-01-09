package br.com.soat11.videosvc.infra.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class VideoTestConsumer {

    // O tópico deve ser o mesmo usado no seu Producer
    @KafkaListener(topics = "video-processing-topic", groupId = "video-group-teste")
    public void listen(Object message) {
        System.out.println("---------------------------------------------------------");
        System.out.println("KAFKA CONSUMER TESTE: Mensagem recebida da fila!");
        System.out.println("CONTEÚDO: " + message.toString());
        System.out.println("---------------------------------------------------------");
    }
}