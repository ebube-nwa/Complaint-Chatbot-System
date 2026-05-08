package dev.ebube.complaintchatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ebube.complaintchatbot.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AIService {

    private static final String SYSTEM_PROMPT =
        "You are a helpful, empathetic customer service assistant. " +
        "For every user message, respond with a JSON object containing exactly these fields:\n" +
        "  \"sentiment\": one of POSITIVE, NEGATIVE, or NEUTRAL\n" +
        "  \"category\": one of PRODUCT, SERVICE, BILLING, SHIPPING, TECHNICAL, or OTHER\n" +
        "  \"response\": your empathetic reply to the customer\n" +
        "Output only the raw JSON object with no markdown, no code fences, and no extra text.";

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIService() {
        this.webClient = WebClient.builder().build();
    }

    public Map<String, String> analyzeComplaint(String message) {
        try {
            String raw = callOpenAI(List.of(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", message)
            ));
            return parseAnalysis(raw);
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            return fallback("Thank you for your feedback. We're reviewing your message and will get back to you soon.");
        }
    }

    public Map<String, String> generateContextualResponse(String userMessage, List<Message> conversationHistory) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
            for (Message msg : conversationHistory) {
                String role = "USER".equals(msg.getSender()) ? "user" : "assistant";
                messages.add(Map.of("role", role, "content", msg.getContent()));
            }
            messages.add(Map.of("role", "user", "content", userMessage));

            String raw = callOpenAI(messages);
            return parseAnalysis(raw);
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            return fallback("I understand. How else can I help you?");
        }
    }

    private String callOpenAI(List<Map<String, String>> messages) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 300);
        requestBody.put("temperature", 0.7);
        requestBody.put("response_format", Map.of("type", "json_object"));

        Mono<Map> responseMono = webClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class);

        Map response = responseMono.block();
        if (response != null && response.containsKey("choices")) {
            List<Map> choices = (List<Map>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        return "{}";
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseAnalysis(String json) throws Exception {
        Map<String, Object> parsed = objectMapper.readValue(json, Map.class);
        Map<String, String> result = new HashMap<>();
        result.put("sentiment", String.valueOf(parsed.getOrDefault("sentiment", "NEUTRAL")).trim().toUpperCase());
        result.put("category", String.valueOf(parsed.getOrDefault("category", "OTHER")).trim().toUpperCase());
        result.put("response", String.valueOf(parsed.getOrDefault("response", "I understand. How else can I help you?")).trim());
        return result;
    }

    private Map<String, String> fallback(String responseText) {
        Map<String, String> result = new HashMap<>();
        result.put("sentiment", "NEUTRAL");
        result.put("category", "OTHER");
        result.put("response", responseText);
        return result;
    }
}