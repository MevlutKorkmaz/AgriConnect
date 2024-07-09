package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserId(String userId);
    List<Post> findByCategoryTagsContaining(String tag); // Find posts by category tags
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
