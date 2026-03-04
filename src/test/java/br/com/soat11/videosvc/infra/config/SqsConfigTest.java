package br.com.soat11.videosvc.infra.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SqsConfig.class)
class SqsConfigTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void sqsClientBean_DeveSerCriadoNoContexto() {
        SqsClient sqsClient = context.getBean(SqsClient.class);
        assertNotNull(sqsClient);
        assertTrue(sqsClient instanceof SqsClient);
    }
}
