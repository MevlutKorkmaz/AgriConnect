package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    // Find all comments by a specific user
    List<Comment> findByUserId(String userId);

    // Find all comments for a specific post or product
    List<Comment> findByPostId(String postId);
    List<Comment> findByQuestionId(String questionId);

    // Find all replies for a specific comment
    List<Comment> findByParentCommentId(String parentCommentId);
}
