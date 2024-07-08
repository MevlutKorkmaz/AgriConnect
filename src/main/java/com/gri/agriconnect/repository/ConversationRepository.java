package com.gri.agriconnect.repository;


import com.gri.agriconnect.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findBySenderIdOrReceiverId(String senderId, String receiverId);
    Conversation findByConversationId(String conversationId);
}
