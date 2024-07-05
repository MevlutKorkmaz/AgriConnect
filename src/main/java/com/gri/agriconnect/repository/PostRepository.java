package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}
