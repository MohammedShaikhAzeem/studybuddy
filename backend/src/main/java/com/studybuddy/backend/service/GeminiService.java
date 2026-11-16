package com.studybuddy.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestClient restClient = RestClient.create();

    @SuppressWarnings("unchecked")
    public String generate(String systemPrompt, String userMessage) {
        Map<String, Object> body = new HashMap<>();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            body.put("systemInstruction",
                    Map.of("parts", List.of(Map.of("text", systemPrompt))));
        }

        body.put("contents", List.of(
                Map.of("role", "user",
                        "parts", List.of(Map.of("text", userMessage)))));
        

        Map<String, Object> response = restClient.post()
                .uri(apiUrl)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        try {
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new RuntimeException("Could not read Gemini response");
        }
        
        
    }
}