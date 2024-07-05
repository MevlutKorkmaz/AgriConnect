package com.gri.agriconnect.model;

import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "users")
public class User {
    @Id
    private String userId;

    @NotBlank
    private String accountName;
    private String fullName;

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 4, max = 15)
    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private String phoneNo;
    private String location;
    private Integer followerCount = 0;
    private Integer followingCount = 0;
    private Integer conversationCount = 0;
    private Integer productCount = 0;
    private Integer postCount = 0;

    private List<String> followerIds = new ArrayList<>();
    private List<String> followingIds = new ArrayList<>();
    private List<String> conversationIds = new ArrayList<>(); // List of conversation IDs the user is part of
    private List<String> productIds = new ArrayList<>();
    private List<String> postIds = new ArrayList<>();
    // Add follower
    public void addFollower(String followerId) {
        if (!followerIds.contains(followerId)) {
            followerIds.add(followerId);
            followerCount++;
        }
    }

    // Remove follower
    public void removeFollower(String followerId) {
        if (followerIds.contains(followerId)) {
            followerIds.remove(followerId);
            followerCount--;
        }
    }

    // Add following
    public void addFollowing(String followingId) {
        if (!followingIds.contains(followingId)) {
            followingIds.add(followingId);
            followingCount++;
        }
    }

    // Remove following
    public void removeFollowing(String followingId) {
        if (followingIds.contains(followingId)) {
            followingIds.remove(followingId);
            followingCount--;
        }
    }

    // Add conversation
    public void addConversation(String conversationId) {
        if (!conversationIds.contains(conversationId)) {
            conversationIds.add(conversationId);
            conversationCount++;
        }
    }

    // Remove conversation
    public void removeConversation(String conversationId) {
        if (conversationIds.contains(conversationId)) {
            conversationIds.remove(conversationId);
            conversationCount--;
        }
    }

    // Add product
    public void addProduct(String productId) {
        if (!productIds.contains(productId)) {
            productIds.add(productId);
            productCount++;
        }
    }

    // Remove product
    public void removeProduct(String productId) {
        if (productIds.contains(productId)) {
            productIds.remove(productId);
            productCount--;
        }
    }

    // Add post
    public void addPost(String postId) {
        if (!postIds.contains(postId)) {
            postIds.add(postId);
            postCount++;
        }
    }

    // Remove post
    public void removePost(String postId) {
        if (postIds.contains(postId)) {
            postIds.remove(postId);
            postCount--;
        }
    }
}






