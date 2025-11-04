package dev.ebube.complaintchatbot.repository;

import dev.ebube.complaintchatbot.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findBySessionId(String sessionId);
}