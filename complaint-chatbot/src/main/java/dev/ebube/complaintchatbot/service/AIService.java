package dev.ebube.complaintchatbot.service;

import dev.ebube.complaintchatbot.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Value("${openai.api.url}")
    private String apiUrl;
    
    private final WebClient webClient;
    
    public AIService() {
        this.webClient = WebClient.builder().build();
    }
    
    // Original method for single complaint analysis
    public Map<String, String> analyzeComplaint(String message) {
        Map<String, String> analysis = new HashMap<>();
        
        try {
            String sentiment = callOpenAI(
                "Analyze the sentiment of this customer message and respond with only one word: POSITIVE, NEGATIVE, or NEUTRAL.\n\nMessage: " + message
            );
            analysis.put("sentiment", sentiment.trim().toUpperCase());
            
            String category = callOpenAI(
                "Categorize this customer complaint into one of these categories (respond with only the category name): PRODUCT, SERVICE, BILLING, SHIPPING, TECHNICAL, OTHER.\n\nMessage: " + message
            );
            analysis.put("category", category.trim().toUpperCase());
            
            String response = callOpenAI(
                "You are a helpful customer service assistant. Write a brief, empathetic response to this customer complaint:\n\n" + message
            );
            analysis.put("response", response.trim());
            
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            analysis.put("sentiment", "NEUTRAL");
            analysis.put("category", "UNCATEGORIZED");
            analysis.put("response", "Thank you for your feedback. We're reviewing your message and will get back to you soon.");
        }
        
        return analysis;
    }
    
    // New method for conversation-based responses with context
    public Map<String, String> generateContextualResponse(String userMessage, List<Message> conversationHistory) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // Build conversation context
            StringBuilder context = new StringBuilder();
            context.append("Previous conversation:\n");
            for (Message msg : conversationHistory) {
                context.append(msg.getSender()).append(": ").append(msg.getContent()).append("\n");
            }
            context.append("\nUser: ").append(userMessage);
            
            // Analyze sentiment
            String sentiment = callOpenAI(
                "Analyze the sentiment of this customer message and respond with only one word: POSITIVE, NEGATIVE, or NEUTRAL.\n\nMessage: " + userMessage
            );
            result.put("sentiment", sentiment.trim().toUpperCase());
            
            // Categorize
            String category = callOpenAI(
                "Categorize this customer message into one of these categories (respond with only the category name): PRODUCT, SERVICE, BILLING, SHIPPING, TECHNICAL, OTHER.\n\nMessage: " + userMessage
            );
            result.put("category", category.trim().toUpperCase());
            
            // Generate contextual response
            String response = callOpenAIWithContext(context.toString());
            result.put("response", response.trim());
            
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            result.put("sentiment", "NEUTRAL");
            result.put("category", "OTHER");
            result.put("response", "I understand. How else can I help you?");
        }
        
        return result;
    }
    
    private String callOpenAI(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("max_tokens", 150);
        requestBody.put("temperature", 0.7);
        
        return makeAPICall(requestBody);
    }
    
    private String callOpenAIWithContext(String conversationContext) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
            Map.of("role", "system", "content", "You are a helpful, empathetic customer service assistant. Respond naturally to the customer based on the conversation history."),
            Map.of("role", "user", "content", conversationContext)
        ));
        requestBody.put("max_tokens", 200);
        requestBody.put("temperature", 0.8);
        
        return makeAPICall(requestBody);
    }
    
    private String makeAPICall(Map<String, Object> requestBody) {
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
        
        return "Unable to process";
    }
}