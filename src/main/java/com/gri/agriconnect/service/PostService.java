package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post savePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
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
        postRepository.deleteById(postId);
    }

    public Post updatePost(String postId, Post updatedPost) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setFavoriteCount(updatedPost.getFavoriteCount());
            post.setLikeCount(updatedPost.getLikeCount());
            post.setCommentCount(updatedPost.getCommentCount());
            post.setCommentIds(updatedPost.getCommentIds());
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

