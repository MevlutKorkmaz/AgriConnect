package com.gri.agriconnect.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "questions")
public class Question {
    @Id
    private String questionId;

    @NotNull(message = "Title cannot be null")
    private String title;

    @NotBlank(message = "Body cannot be blank")
    @Size(min = 220, message = "Body must be at least 220 characters")
    private String body;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    private Integer favoriteCount;
    private Integer likeCount;
    private Integer commentCount;

    @Size(max = 15)
    private List<String> categoryTags;

    @Size(max = 5)
    private List<String> imageIds; // Store image IDs

    private List<String> commentIds;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Custom constructor for mandatory fields
    public Question(String title, String body, String userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.favoriteCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.categoryTags = new ArrayList<>();
        this.imageIds = new ArrayList<>();
        this.commentIds = new ArrayList<>();
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
    public void addImage(String imageId) {
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

    // Add tag
    public void addTag(String tag) {
        if (categoryTags.size() < 5 && !categoryTags.contains(tag)) {
            categoryTags.add(tag);
        }
    }

    // Remove tag
    public void removeTag(String tag) {
        categoryTags.remove(tag);
    }

    // Increase favorite count
    public void incrementFavoriteCount() {
        favoriteCount++;
    }

    // Increase like count
    public void incrementLikeCount() {
        likeCount++;
    }
}
