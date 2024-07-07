package com.gri.agriconnect.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    @NotBlank
    private String accountName;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 4, max = 15, message = "Password must be between 4 and 15 characters")
    private String password;

    private Boolean accountLocked;
    private Boolean enabled;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private String phoneNo;
    private String location;

    @PositiveOrZero
    private Integer followerCount;

    @PositiveOrZero
    private Integer followingCount;

    @PositiveOrZero
    private Integer conversationCount;

    @PositiveOrZero
    private Integer productCount;

    @PositiveOrZero
    private Integer postCount;

    private List<String> followerIds;
    private List<String> followingIds;
    private List<String> conversationIds;
    private List<String> productIds;
    private List<String> postIds;

    // Custom constructor for mandatory fields
    public User(String accountName, String firstName, String lastName, String email, String password) {
        this.accountName = accountName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.accountLocked = false;
        this.enabled = true;
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.followerCount = 0;
        this.followingCount = 0;
        this.conversationCount = 0;
        this.productCount = 0;
        this.postCount = 0;
        this.followerIds = new ArrayList<>();
        this.followingIds = new ArrayList<>();
        this.conversationIds = new ArrayList<>();
        this.productIds = new ArrayList<>();
        this.postIds = new ArrayList<>();
    }

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

    // Get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
