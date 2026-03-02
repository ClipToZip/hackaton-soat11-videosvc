package br.com.soat11.videosvc.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JacksonConfig.class)
class JacksonConfigTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void objectMapper_DeveTerJavaTimeModuleRegistrado () {
        assertTrue(objectMapper.getRegisteredModuleIds().stream()
                .anyMatch(id -> id.toString().contains("jsr310")));
    }

    @Test
    void objectMapper_DeveSerializarLocalDateTimeComoISO8601 () throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 1, 12, 34, 56);
        String json = objectMapper.writeValueAsString(dateTime);
        // Deve estar no formato ISO-8601
        assertTrue(json.contains("2026-03-01T12:34:56"));
        assertFalse(json.matches(".*\\d{13,}.*")); // Não deve ser timestamp
    }

    @Test
    void objectMapper_DeveTerFeatureWriteDatesAsTimestampsDesabilitada () {
        assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }
}