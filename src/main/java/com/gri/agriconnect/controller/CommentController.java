package com.gri.agriconnect.controller;

import com.gri.agriconnect.dto.CommentDTO;
import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@Validated
@Tag(name = "Comment", description = "API for managing comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Create a new comment", description = "Adds a new comment to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create")
    public ResponseEntity<Comment> createComment(@Valid @RequestBody CommentDTO commentDTO) {
        logger.info("Creating a new comment");
        try {
            Comment createdComment = commentService.saveComment(commentDTO);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get all comments", description = "Fetches all comments in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched",
                    content = @Content(schema = @Schema(implementation = Comment.class)))
    })
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        logger.info("Fetching all comments");
        List<Comment> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get comments by user ID", description = "Fetches comments by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "No comments found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable String userId) {
        logger.info("Fetching comments for user ID: {}", userId);
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comments found for user ID: " + userId);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get comments by post ID", description = "Fetches comments by post ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "No comments found")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String postId) {
        logger.info("Fetching comments for post ID: {}", postId);
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comments found for post ID: " + postId);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get comments by question ID", description = "Fetches comments by question ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "No comments found")
    })
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Comment>> getCommentsByQuestionId(@PathVariable String questionId) {
        logger.info("Fetching comments for question ID: {}", questionId);
        List<Comment> comments = commentService.getCommentsByQuestionId(questionId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comments found for question ID: " + questionId);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get comment by ID", description = "Fetches a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment fetched",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String commentId) {
        logger.info("Fetching comment with ID: {}", commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found with ID: " + commentId));
    }

    @Operation(summary = "Delete comment", description = "Deletes a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
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

    @Operation(summary = "Update comment", description = "Updates a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        logger.info("Updating comment with ID: {}", commentId);
        try {
            Comment updatedComment = commentService.updateComment(commentId, commentDTO);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Reply to comment", description = "Replies to a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reply created",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<Comment> replyToComment(@PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        logger.info("Replying to comment with ID: {}", commentId);
        try {
            Comment reply = commentService.replyToComment(commentId, commentDTO);
            return new ResponseEntity<>(reply, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get replies for a comment", description = "Fetches replies for a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replies fetched",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getRepliesForComment(@PathVariable String commentId) {
        logger.info("Fetching replies for comment with ID: {}", commentId);
        List<Comment> replies = commentService.getRepliesForComment(commentId);
        if (replies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No replies found for comment ID: " + commentId);
        }
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @Operation(summary = "Like a comment", description = "Likes a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment liked"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable String commentId) {
        logger.info("Liking comment with ID: {}", commentId);
        try {
            Comment likedComment = commentService.likeComment(commentId);
            return new ResponseEntity<>(likedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Unlike a comment", description = "Unlikes a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment unliked"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PostMapping("/{commentId}/unlike")
    public ResponseEntity<Comment> unlikeComment(@PathVariable String commentId) {
        logger.info("Unliking comment with ID: {}", commentId);
        try {
            Comment unlikedComment = commentService.unlikeComment(commentId);
            return new ResponseEntity<>(unlikedComment, HttpStatus.OK);
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
