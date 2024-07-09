package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.model.Product;
import com.gri.agriconnect.repository.CommentRepository;
import com.gri.agriconnect.repository.PostRepository;
import com.gri.agriconnect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final ProductService productService;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostService postService, ProductService productService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.productService = productService;
    }

    public Comment saveComment(Comment comment) {
        boolean isValid = false;

        Optional<Post> postOpt = postService.getPostById(comment.getPostId());
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            Comment savedComment = commentRepository.save(comment);
            post.getCommentIds().add(savedComment.getCommentId());
            postService.updatePost(post.getPostId(), post);
            isValid = true;
        }

        Optional<Product> productOpt = productService.getProductById(comment.getPostId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Comment savedComment = commentRepository.save(comment);
            product.getCommentIds().add(savedComment.getCommentId());
            productService.updateProduct(product.getProductId(), product);
            isValid = true;
        }

        if (!isValid) {
            throw new IllegalArgumentException("Post or Product with ID " + comment.getPostId() + " does not exist.");
        }

        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteComment(String commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();

            Optional<Post> postOpt = postService.getPostById(comment.getPostId());
            if (postOpt.isPresent()) {
                Post post = postOpt.get();
                post.getCommentIds().remove(commentId);
                postService.updatePost(post.getPostId(), post);
            }

            Optional<Product> productOpt = productService.getProductById(comment.getPostId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.getCommentIds().remove(commentId);
                productService.updateProduct(product.getProductId(), product);
            }

            commentRepository.deleteById(commentId);
        } else {
            throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
        }
    }

    public Comment updateComment(String commentId, Comment updatedComment) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setContent(updatedComment.getContent());
            comment.setLikeCount(updatedComment.getLikeCount());
            comment.setUpdatedAt(LocalDateTime.now());
            comment.markAsEdited(updatedComment.getContent()); // Use the new method for marking as edited
            return commentRepository.save(comment);
        }).orElseGet(() -> {
            updatedComment.setCommentId(commentId);
            updatedComment.setCreatedAt(LocalDateTime.now());
            updatedComment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(updatedComment);
        });
    }

    // New method to reply to a comment
    public Comment replyToComment(String parentCommentId, Comment reply) {
        Optional<Comment> parentCommentOpt = commentRepository.findById(parentCommentId);
        if (parentCommentOpt.isPresent()) {
            Comment parentComment = parentCommentOpt.get();
            reply.setParentCommentId(parentCommentId);
            reply.setCreatedAt(LocalDateTime.now());
            reply.setUpdatedAt(LocalDateTime.now());
            Comment savedReply = commentRepository.save(reply);

            // Update the post or product with the new reply
            Optional<Post> postOpt = postService.getPostById(reply.getPostId());
            if (postOpt.isPresent()) {
                Post post = postOpt.get();
                post.getCommentIds().add(savedReply.getCommentId());
                postService.updatePost(post.getPostId(), post);
            }

            Optional<Product> productOpt = productService.getProductById(reply.getPostId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.getCommentIds().add(savedReply.getCommentId());
                productService.updateProduct(product.getProductId(), product);
            }

            return savedReply;
        } else {
            throw new IllegalArgumentException("Parent comment with ID " + parentCommentId + " does not exist.");
        }
    }

    // New method to get replies for a specific comment
    public List<Comment> getRepliesForComment(String commentId) {
        return commentRepository.findByParentCommentId(commentId);
    }

    // New method to like a comment
    public Comment likeComment(String commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setLikeCount(comment.getLikeCount() + 1);
            return commentRepository.save(comment);
        }).orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " does not exist."));
    }

    // New method to unlike a comment
    public Comment unlikeComment(String commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setLikeCount(Math.max(comment.getLikeCount() - 1, 0));
            return commentRepository.save(comment);
        }).orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " does not exist."));
    }
}
