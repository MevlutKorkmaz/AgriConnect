package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/comments")
@Validated
@Tag(name = "Comment", description = "Operations related to Comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Create a new comment", description = "This endpoint allows you to create a new comment.")
    @ApiResponse(responseCode = "201", description = "Comment created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)))
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

    @Operation(summary = "Get all comments", description = "Fetch all comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)))
    })
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        logger.info("Fetching all comments");
        List<Comment> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get comments by user ID", description = "Fetch all comments for a given user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "No comments found for user ID")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(
            @Parameter(description = "ID of the user to fetch comments for") @PathVariable String userId) {
        logger.info("Fetching comments for user ID: {}", userId);
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comments found for user ID: " + userId);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get a comment by ID", description = "Fetch a comment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(
            @Parameter(description = "ID of the comment to fetch") @PathVariable String commentId) {
        logger.info("Fetching comment with ID: {}", commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found with ID: " + commentId));
    }

    @Operation(summary = "Delete a comment by ID", description = "Delete a comment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the comment to delete") @PathVariable String commentId) {
        logger.info("Deleting comment with ID: {}", commentId);
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Update a comment", description = "Update the details of an existing comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @Parameter(description = "ID of the comment to update") @PathVariable String commentId,
            @Valid @RequestBody Comment comment) {
        logger.info("Updating comment with ID: {}", commentId);
        try {
            Comment updatedComment = commentService.updateComment(commentId, comment);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Reply to a comment", description = "Reply to an existing comment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reply created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Parent comment not found")
    })
    @PostMapping("/{parentCommentId}/reply")
    public ResponseEntity<Comment> replyToComment(
            @Parameter(description = "ID of the parent comment to reply to") @PathVariable String parentCommentId,
            @Valid @RequestBody Comment reply) {
        logger.info("Replying to comment with ID: {}", parentCommentId);
        try {
            Comment createdReply = commentService.replyToComment(parentCommentId, reply);
            return new ResponseEntity<>(createdReply, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get comments for a post or product", description = "Fetch all comments for a given post or product ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "No comments found for post or product ID")
    })
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(
            @Parameter(description = "ID of the post or product to fetch comments for") @PathVariable String postId) {
        logger.info("Fetching comments for post or product ID: {}", postId);
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        if (comments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comments found for post or product ID: " + postId);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Get replies for a comment", description = "Fetch all replies for a given comment ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of replies",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "No replies found for comment ID")
    })
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getRepliesForComment(
            @Parameter(description = "ID of the comment to fetch replies for") @PathVariable String commentId) {
        logger.info("Fetching replies for comment ID: {}", commentId);
        List<Comment> replies = commentService.getRepliesForComment(commentId);
        if (replies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No replies found for comment ID: " + commentId);
        }
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @Operation(summary = "Like a comment", description = "Like a comment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment liked successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Comment> likeComment(
            @Parameter(description = "ID of the comment to like") @PathVariable String commentId) {
        logger.info("Liking comment with ID: {}", commentId);
        try {
            Comment likedComment = commentService.likeComment(commentId);
            return new ResponseEntity<>(likedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Unlike a comment", description = "Unlike a comment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment unliked successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PostMapping("/{commentId}/unlike")
    public ResponseEntity<Comment> unlikeComment(
            @Parameter(description = "ID of the comment to unlike") @PathVariable String commentId) {
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
