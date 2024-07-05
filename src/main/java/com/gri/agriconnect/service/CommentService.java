package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(Comment comment) {
        comment.setCommendId(null); // Ensure the ID is null so MongoDB can generate it
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Optional<Comment> updateComment(String id, Comment comment) {
        return commentRepository.findById(id).map(existingComment -> {
            existingComment.setContent(comment.getContent());
            existingComment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(existingComment);
        });
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }

    public Optional<Comment> likeComment(String id) {
        return commentRepository.findById(id).map(comment -> {
            comment.setLikeCount(comment.getLikeCount() + 1);
            comment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(comment);
        });
    }
}

