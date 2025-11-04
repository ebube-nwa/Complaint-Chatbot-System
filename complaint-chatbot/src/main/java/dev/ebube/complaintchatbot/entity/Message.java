package dev.ebube.complaintchatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonIgnore  // Prevent circular reference during serialization // Same in Conversation entity
    private Conversation conversation;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @Column(nullable = false)
    private String sender; // USER or BOT
    
    private String sentiment;
    
    private String category;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}