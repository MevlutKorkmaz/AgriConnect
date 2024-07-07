package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Post savePost(Post post) {
        Optional<User> userOpt = userService.getUserById(post.getUserId());
        if (userOpt.isPresent()) {
            Post savedPost = postRepository.save(post);
            userService.addPostToUser(post.getUserId(), savedPost.getPostId());
            return savedPost;
        } else {
            throw new IllegalArgumentException("User with ID " + post.getUserId() + " does not exist.");
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findByUserId(userId);
    }

    public Optional<Post> getPostById(String postId) {
        return postRepository.findById(postId);
    }

    public void deletePost(String postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            userService.removePostFromUser(post.getUserId(), postId);
            postRepository.deleteById(postId);
        } else {
            throw new IllegalArgumentException("Post with ID " + postId + " does not exist.");
        }
    }

    public Post updatePost(String postId, Post updatedPost) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setFavoriteCount(updatedPost.getFavoriteCount());
            post.setLikeCount(updatedPost.getLikeCount());
            post.setCommentCount(updatedPost.getCommentCount());
            post.setCategoryTags(updatedPost.getCategoryTags());
            post.setImageLinks(updatedPost.getImageLinks());
            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post);
        }).orElseGet(() -> {
            updatedPost.setPostId(postId);
            updatedPost.setCreatedAt(LocalDateTime.now());
            updatedPost.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(updatedPost);
        });
    }
}
