package com.gri.agriconnect.repository;


import com.gri.agriconnect.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
}
