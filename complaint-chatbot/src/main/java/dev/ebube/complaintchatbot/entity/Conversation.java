package dev.ebube.complaintchatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId; // Unique identifier for this conversation
    
    private String userId; // Optional: to identify the customer
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime lastMessageAt;
    
    private String status; // ACTIVE, CLOSED, ESCALATED
    
    private String overallSentiment; // Track sentiment across conversation
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    @JsonIgnore // Prevent circular reference during serialization // Also in Message entity
    private List<Message> messages = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
        status = "ACTIVE";
    }
}

