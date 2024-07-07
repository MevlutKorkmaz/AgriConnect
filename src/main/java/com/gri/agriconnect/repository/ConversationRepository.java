package com.gri.agriconnect.repository;


import com.gri.agriconnect.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findBySenderIdOrReceiverId(String senderId, String receiverId);
    Conversation findByConversationId(String conversationId);
}
