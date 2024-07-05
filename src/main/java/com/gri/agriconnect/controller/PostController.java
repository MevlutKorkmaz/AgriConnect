package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
        logger.info("Creating a new post");
        Post createdPost = postService.savePost(post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        logger.info("Fetching all posts");
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable String userId) {
        logger.info("Fetching posts for user ID: {}", userId);
        List<Post> posts = postService.getPostsByUserId(userId);
        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No posts found for user ID: " + userId);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) {
        logger.info("Fetching post with ID: {}", postId);
        Optional<Post> post = postService.getPostById(postId);
        return post.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        logger.info("Deleting post with ID: {}", postId);
        Optional<Post> post = postService.getPostById(postId);
        if (post.isPresent()) {
            postService.deletePost(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId);
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable String postId, @Valid @RequestBody Post post) {
        logger.info("Updating post with ID: {}", postId);
        Optional<Post> existingPost = postService.getPostById(postId);
        if (existingPost.isPresent()) {
            Post updatedPost = postService.updatePost(postId, post);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId);
        }
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> patchPost(@PathVariable String postId, @RequestBody Post post) {
        logger.info("Patching post with ID: {}", postId);
        Optional<Post> existingPost = postService.getPostById(postId);
        if (existingPost.isPresent()) {
            Post updatedPost = existingPost.get();
            if (post.getTitle() != null) {
                updatedPost.setTitle(post.getTitle());
            }
            if (post.getContent() != null) {
                updatedPost.setContent(post.getContent());
            }
            if (post.getFavoriteCount() != updatedPost.getFavoriteCount()) {
                updatedPost.setFavoriteCount(post.getFavoriteCount());
            }
            if (post.getLikeCount() != updatedPost.getLikeCount()) {
                updatedPost.setLikeCount(post.getLikeCount());
            }
            if (post.getCommentCount() != updatedPost.getCommentCount()) {
                updatedPost.setCommentCount(post.getCommentCount());
            }
            if (post.getCommentIds() != null) {
                updatedPost.setCommentIds(post.getCommentIds());
            }
            updatedPost.setUpdatedAt(LocalDateTime.now());
            Post savedPost = postService.savePost(updatedPost);
            return new ResponseEntity<>(savedPost, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId);
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
