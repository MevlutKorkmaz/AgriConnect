package com.gri.agriconnect.controller;

import com.gri.agriconnect.dto.PostDTO;
import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@Validated
@Tag(name = "Post", description = "API for managing posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Create a new post", description = "Adds a new post to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created",
                    content = @Content(schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(
            @Valid @ModelAttribute PostDTO postDTO) throws IOException {
        logger.info("Creating a new post");
        try {
            Post createdPost = postService.createPost(postDTO);
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get all posts", description = "Fetches all posts in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts fetched",
                    content = @Content(schema = @Schema(implementation = Post.class)))
    })
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        logger.info("Fetching all posts");
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Operation(summary = "Get posts by user ID", description = "Fetches posts by their user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts fetched",
                    content = @Content(schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "404", description = "No posts found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable String userId) {
        logger.info("Fetching posts for user ID: {}", userId);
        List<Post> posts = postService.getPostsByUserId(userId);
        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No posts found for user ID: " + userId);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Operation(summary = "Get post by ID", description = "Fetches a post by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post fetched",
                    content = @Content(schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) {
        logger.info("Fetching post with ID: {}", postId);
        Optional<Post> post = postService.getPostById(postId);
        return post.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId));
    }

    @Operation(summary = "Delete post", description = "Deletes a post by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        logger.info("Deleting post with ID: {}", postId);
        try {
            postService.deletePost(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Update post", description = "Updates a post by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated",
                    content = @Content(schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable String postId, @Valid @RequestBody PostDTO postDTO) throws IOException {
        logger.info("Updating post with ID: {}", postId);
        try {
            Post updatedPost = postService.updatePost(postId, postDTO);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Search posts by tag", description = "Fetch posts by a specific tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of posts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "404", description = "No posts found for the tag")
    })
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Post>> searchPostsByTag(
            @Parameter(description = "Tag to search posts by") @PathVariable String tag) {
        logger.info("Searching posts by tag: {}", tag);
        List<Post> posts = postService.searchPostsByTag(tag);
        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No posts found for the tag: " + tag);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Operation(summary = "Search posts by title or content", description = "Fetch posts by title or content.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of posts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "404", description = "No posts found for the search text")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPostsByTitleOrContent(
            @Parameter(description = "Search text for title or content") @RequestParam String searchText) {
        logger.info("Searching posts by title or content: {}", searchText);
        List<Post> posts = postService.searchPostsByTitleOrContent(searchText);
        if (posts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No posts found for the search text: " + searchText);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Operation(summary = "Like a post", description = "Like a post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post liked successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping("/{postId}/like")
    public ResponseEntity<Post> likePost(
            @Parameter(description = "ID of the post to like") @PathVariable String postId) {
        logger.info("Liking post with ID: {}", postId);
        try {
            Post likedPost = postService.likePost(postId);
            return new ResponseEntity<>(likedPost, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Unlike a post", description = "Unlike a post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post unliked successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<Post> unlikePost(
            @Parameter(description = "ID of the post to unlike") @PathVariable String postId) {
        logger.info("Unliking post with ID: {}", postId);
        try {
            Post unlikedPost = postService.unlikePost(postId);
            return new ResponseEntity<>(unlikedPost, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Add images to post", description = "Adds multiple images to a post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images added successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping("/{postId}/images")
    public ResponseEntity<Void> addImages(
            @Parameter(description = "ID of the post to add images to") @PathVariable String postId,
            @RequestParam("files") MultipartFile[] images) {
        logger.info("Adding images to post with ID: {}", postId);
        try {
            postService.addImages(postId, images);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload images");
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
