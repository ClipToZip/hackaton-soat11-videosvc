package br.com.soat11.videosvc.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtDecoderTest {
    @Test
    void extractSubWithoutValidation_DeveExtrairSubCorretamente() throws Exception {
        // Header: {"alg":"none"}
        // Payload: {"sub":"1234567890","name":"John Doe"}
        String header = "{\"alg\":\"none\"}";
        String payload = "{\"sub\":\"1234567890\",\"name\":\"John Doe\"}";
        String encodedHeader = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String token = encodedHeader + "." + encodedPayload + ".";

        String sub = JwtDecoder.extractSubWithoutValidation(token);
        assertEquals("1234567890", sub);
    }

    @Test
    void extractSubWithoutValidation_DeveLancarExceptionParaTokenInvalido() {
        String token = "invalid.token";
        assertThrows(Exception.class, () -> JwtDecoder.extractSubWithoutValidation(token));
    }

    @Test
    void extractSubWithoutValidation_DeveRetornarNullSeNaoTiverSub() throws Exception {
        String header = "{\"alg\":\"none\"}";
        String payload = "{\"name\":\"John Doe\"}";
        String encodedHeader = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String token = encodedHeader + "." + encodedPayload + ".";

        String sub = JwtDecoder.extractSubWithoutValidation(token);
        assertNull(sub);

    }
}