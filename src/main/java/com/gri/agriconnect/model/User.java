package com.gri.agriconnect.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data

@Document(collection = "users")
public class User {

    @Id
    private String userId;

    @NotBlank(message = "First name cannot be blank")
    private String name;

//    @NotBlank(message = "Last name cannot be blank")
//    private String lastName;

    @NotBlank
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;
    @NotBlank
    @Size(min = 4, max = 15, message = "Password must be between 4 and 15 characters")
    private String password;

    private Boolean accountLocked;
    private Boolean accountEnabled;

    private Boolean emailVerified;
    private String emailVerificationToken;

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
    private Integer questionCount;

    @PositiveOrZero
    private Integer postCount;

    private List<String> followerIds;
    private List<String> followingIds;
    private List<String> questionIds;
    private List<String> postIds;

    private String profilePhotoId;  // Store the ID of the profile photo
    private Boolean privateAccount;  // Indicates if the account is private

    // Additional fields
    private String bio;
    private String coverPhotoId;  // Store the ID of the cover photo
    private LocalDate dateOfBirth;
    private List<String> socialMediaLinks;
    private List<String> interests;
    private Boolean emailNotificationsEnabled;

    // Custom constructor for mandatory fields
    public User(String name, String email, String password) {
        this.name = name;
//        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.accountLocked = false;
        this.accountEnabled = false; // Initially false, needs email verification
        this.emailVerified = false; // Initially false, needs email verification
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.followerCount = 0;
        this.followingCount = 0;
        this.questionCount = 0;
        this.postCount = 0;
        this.followerIds = new ArrayList<>();
        this.followingIds = new ArrayList<>();
        this.questionIds = new ArrayList<>();
        this.postIds = new ArrayList<>();
        this.privateAccount = false;
        this.socialMediaLinks = new ArrayList<>();
        this.interests = new ArrayList<>();
        this.bio = "";
        this.coverPhotoId = null;
        this.dateOfBirth = null;
        this.emailNotificationsEnabled = true;
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

    // Add question
    public void addQuestion(String questionId) {
        if (!questionIds.contains(questionId)) {
            questionIds.add(questionId);
            questionCount++;
        }
    }

    // Remove question
    public void removeQuestion(String questionId) {
        if (questionIds.contains(questionId)) {
            questionIds.remove(questionId);
            questionCount--;
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
//    public String getFullName() {
//        return firstName + " " + lastName;
//    }

    // Check if a user can view the profile
    public boolean canViewProfile(String viewerId) {
        return !privateAccount || followerIds.contains(viewerId);
    }

    // Add interest
    public void addInterest(String interest) {
        if (!interests.contains(interest)) {
            interests.add(interest);
        }
    }

    // Remove interest
    public void removeInterest(String interest) {
        interests.remove(interest);
    }

    // Add social media link
    public void addSocialMediaLink(String link) {
        if (!socialMediaLinks.contains(link)) {
            socialMediaLinks.add(link);
        }
    }

    // Remove social media link
    public void removeSocialMediaLink(String link) {
        socialMediaLinks.remove(link);
    }
}
