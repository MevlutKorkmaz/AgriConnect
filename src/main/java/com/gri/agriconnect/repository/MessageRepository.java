package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message,String> {
}
