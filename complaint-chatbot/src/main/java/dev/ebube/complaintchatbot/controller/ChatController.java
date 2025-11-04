package dev.ebube.complaintchatbot.controller;

import dev.ebube.complaintchatbot.entity.Conversation;
import dev.ebube.complaintchatbot.entity.Message;
import dev.ebube.complaintchatbot.service.ConversationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    @Autowired
    private ConversationService conversationService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startConversation(@RequestBody StartChatRequest request) {
        Conversation conversation = conversationService.createConversation(request.getUserId());
        
        Map<String, String> response = new HashMap<>();
        response.put("sessionId", conversation.getSessionId());
        response.put("message", "Hello! How can I help you today?");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/message")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody ChatRequest request) {
        Message botResponse = conversationService.sendMessage(
            request.getSessionId(),
            request.getMessage(),
            request.getUserId()
        );
        
        MessageResponse response = new MessageResponse();
        response.setSessionId(request.getSessionId());
        response.setMessage(botResponse.getContent());
        response.setSender("BOT");
        response.setTimestamp(botResponse.getTimestamp().toString());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Message>> getHistory(@PathVariable String sessionId) {
        List<Message> history = conversationService.getConversationHistory(sessionId);
        return ResponseEntity.ok(history);
    }
    
    // DTOs
    public static class StartChatRequest {
        private String userId;
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    public static class ChatRequest {
        private String sessionId;
        private String message;
        private String userId;
        
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    public static class MessageResponse {
        private String sessionId;
        private String message;
        private String sender;
        private String timestamp;
        
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getSender() {
            return sender;
        }
        
        public void setSender(String sender) {
            this.sender = sender;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
