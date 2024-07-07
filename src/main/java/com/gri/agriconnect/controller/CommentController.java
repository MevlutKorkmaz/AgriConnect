package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@Validated
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
        logger.info("Creating a new comment");
        try {
            Comment createdComment = commentService.saveComment(comment);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        logger.info("Fetching all comments");
        List<Comment> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable String userId) {
        logger.info("Fetching comments for user ID: {}", userId);
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comments found for user ID: " + userId);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String commentId) {
        logger.info("Fetching comment with ID: {}", commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found with ID: " + commentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        logger.info("Deleting comment with ID: {}", commentId);
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String commentId, @Valid @RequestBody Comment comment) {
        logger.info("Updating comment with ID: {}", commentId);
        try {
            Comment updatedComment = commentService.updateComment(commentId, comment);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> patchComment(@PathVariable String commentId, @RequestBody Comment comment) {
        logger.info("Patching comment with ID: {}", commentId);
        try {
            Comment updatedComment = commentService.updateComment(commentId, comment);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
