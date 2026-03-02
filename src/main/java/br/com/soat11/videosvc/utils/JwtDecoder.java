package br.com.soat11.videosvc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtDecoder {

    public static String extractSubWithoutValidation(String token) throws Exception {

        String[] parts = token.split("\\.");
        String payload = parts[1];

        byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
        String json = new String(decodedBytes);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> claims = mapper.readValue(json, Map.class);

        return (String) claims.get("sub");
    }
}
