package com.gri.agriconnect.model;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "posts")
public class Post {

    @Id
    private String postId;

    @NotBlank
    private String userId;

    @NotBlank
    private String title;

    @Size(max = 2000)
    private String content;

    private Integer favoriteCount;
    private Integer likeCount;
    private Integer commentCount;

    @Size(max = 15)
    private List<String> categoryTags;

    @Size(max = 5)
    private List<String> imageIds; // Store image IDs instead of images directly in the entity

    private List<String> commentIds;

    private Boolean isPrivate; // Indicates if the post is private
    private String location; // Location information for the post
    private Integer shareCount; // Count of how many times the post is shared

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Custom constructor for mandatory fields
    public Post(String userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.favoriteCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.commentIds = new ArrayList<>();
        this.categoryTags = new ArrayList<>();
        this.imageIds = new ArrayList<>();
        this.isPrivate = false;
        this.location = "";
        this.shareCount = 0;
    }

    // Add category tag
    public void addCategoryTag(String tag) {
        if (categoryTags.size() < 15 && !categoryTags.contains(tag)) {
            categoryTags.add(tag);
        }
    }

    // Remove category tag
    public void removeCategoryTag(String tag) {
        categoryTags.remove(tag);
    }

    // Add image
    public void addImageId(String imageId) {
        if (imageIds.size() < 5) {
            imageIds.add(imageId);
        }
    }

    // Remove image
    public void removeImage(String imageId) {
        imageIds.remove(imageId);
    }

    // Add comment
    public void addComment(String commentId) {
        commentIds.add(commentId);
        commentCount++;
    }

    // Remove comment
    public void removeComment(String commentId) {
        commentIds.remove(commentId);
        commentCount--;
    }

    // Increase share count
    public void incrementShareCount() {
        shareCount++;
    }
}
