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
@Document(collection = "products")
public class Product {
    @Id
    private String productId;

    @NotNull(message = "Name cannot be null")
    private String name;

    @Size(max = 2000)
    private String description;

    private Double price;

    @NotBlank
    private String supplierId;

    private Integer stockQuantity;
    private Integer favoriteCount;
    private Integer likeCount;
    private Integer commentCount;

    @Size(max = 15)
    private List<String> categoryTags;

    @Size(max = 5)
    private List<String> imageIds; // Store image IDs

    private List<String> commentIds;

    private Boolean isPrivate; // Indicates if the product is private
    private String location; // Location information for the product
    private Integer shareCount; // Count of how many times the product is shared

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Custom constructor for mandatory fields
    public Product(String name, String supplierId, List<String> categoryTags) {
        this.name = name;
        this.supplierId = supplierId;
        this.categoryTags = categoryTags;
        this.favoriteCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.commentIds = new ArrayList<>();
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

    // Increase share count
    public void incrementShareCount() {
        shareCount++;
    }
}
