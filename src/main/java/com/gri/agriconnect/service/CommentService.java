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

    public Comment saveComment(Comment comment) {
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment updateComment(String commentId, Comment updatedComment) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setContent(updatedComment.getContent());
            comment.setLikeCount(updatedComment.getLikeCount());
            comment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(comment);
        }).orElseGet(() -> {
            updatedComment.setCommentId(commentId);
            updatedComment.setCreatedAt(LocalDateTime.now());
            updatedComment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(updatedComment);
        });
    }
}


