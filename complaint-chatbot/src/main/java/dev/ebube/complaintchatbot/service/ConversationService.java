package dev.ebube.complaintchatbot.service;

import dev.ebube.complaintchatbot.entity.Conversation;
import dev.ebube.complaintchatbot.entity.Message;
import dev.ebube.complaintchatbot.repository.ConversationRepository;
import dev.ebube.complaintchatbot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private AIService aiService;
    
    @Transactional
    public Conversation createConversation(String userId) {
        Conversation conversation = new Conversation();
        conversation.setSessionId(UUID.randomUUID().toString());
        conversation.setUserId(userId);
        conversation.setStatus("ACTIVE");
        return conversationRepository.save(conversation);
    }
    
    @Transactional
    public Message sendMessage(String sessionId, String messageContent, String userId) {
        // Find or create conversation
        Conversation conversation = conversationRepository.findBySessionId(sessionId)
            .orElseGet(() -> {
                Conversation newConv = new Conversation();
                newConv.setSessionId(sessionId);
                newConv.setUserId(userId);
                newConv.setStatus("ACTIVE");
                return conversationRepository.save(newConv);
            });
        
        // Save user message
        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setContent(messageContent);
        userMessage.setSender("USER");
        messageRepository.save(userMessage);
        
        // Get conversation history (excluding the message we just added)
        List<Message> history = messageRepository.findByConversationIdOrderByTimestampAsc(conversation.getId());
        
        // Generate AI response with context
        Map<String, String> aiAnalysis = aiService.generateContextualResponse(messageContent, history);
        
        // Update user message with analysis
        userMessage.setSentiment(aiAnalysis.get("sentiment"));
        userMessage.setCategory(aiAnalysis.get("category"));
        messageRepository.save(userMessage);
        
        // Create bot response message
        Message botMessage = new Message();
        botMessage.setConversation(conversation);
        botMessage.setContent(aiAnalysis.get("response"));
        botMessage.setSender("BOT");
        botMessage.setSentiment("NEUTRAL");
        messageRepository.save(botMessage);
        
        // Update conversation metadata
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return botMessage;
    }
    
    public Conversation getConversation(String sessionId) {
        return conversationRepository.findBySessionId(sessionId).orElse(null);
    }
    
    public List<Message> getConversationHistory(String sessionId) {
        Conversation conversation = conversationRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversation.getId());
    }
}