package dev.ebube.complaintchatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ebube.complaintchatbot.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private static final String SYSTEM_PROMPT = """
            You are a helpful, empathetic customer service assistant.
            For every user message, respond with a single JSON object and nothing else, with this exact shape:
            {
              "sentiment": "POSITIVE" | "NEGATIVE" | "NEUTRAL",
              "category": "PRODUCT" | "SERVICE" | "BILLING" | "SHIPPING" | "TECHNICAL" | "OTHER",
              "response": "<a brief, empathetic reply to the user>"
            }
            The reply in "response" should read naturally to the customer and continue the conversation if there is prior context.
            """;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model:gpt-4o-mini}")
    private String model;

    private final WebClient webClient = WebClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> analyzeComplaint(String message) {
        return analyze(message, List.of());
    }

    public Map<String, String> generateContextualResponse(String userMessage, List<Message> conversationHistory) {
        return analyze(userMessage, conversationHistory);
    }

    private Map<String, String> analyze(String userMessage, List<Message> history) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
            for (Message msg : history) {
                String role = "BOT".equalsIgnoreCase(msg.getSender()) ? "assistant" : "user";
                messages.add(Map.of("role", role, "content", msg.getContent()));
            }
            messages.add(Map.of("role", "user", "content", userMessage));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0.7);
            requestBody.put("response_format", Map.of("type", "json_object"));

            String content = callOpenAI(requestBody);
            return parseAnalysis(content);
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            return fallback();
        }
    }

    private String callOpenAI(Map<String, Object> requestBody) {
        @SuppressWarnings("rawtypes")
        Map response = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.get("choices") instanceof List<?> choices && !choices.isEmpty()) {
            Object first = choices.get(0);
            if (first instanceof Map<?, ?> choice && choice.get("message") instanceof Map<?, ?> message) {
                Object content = message.get("content");
                if (content instanceof String s) {
                    return s;
                }
            }
        }
        throw new IllegalStateException("Unexpected OpenAI response shape");
    }

    private Map<String, String> parseAnalysis(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        Map<String, String> result = new HashMap<>();
        result.put("sentiment", normalize(node.path("sentiment").asText("NEUTRAL"), "NEUTRAL"));
        result.put("category", normalize(node.path("category").asText("OTHER"), "OTHER"));
        String reply = node.path("response").asText("").trim();
        result.put("response", reply.isEmpty() ? "Thanks for reaching out — could you tell me a bit more?" : reply);
        return result;
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value.trim().toUpperCase();
    }

    private Map<String, String> fallback() {
        Map<String, String> result = new HashMap<>();
        result.put("sentiment", "NEUTRAL");
        result.put("category", "OTHER");
        result.put("response", "Thank you for your message. We're reviewing it and will get back to you soon.");
        return result;
    }
}
