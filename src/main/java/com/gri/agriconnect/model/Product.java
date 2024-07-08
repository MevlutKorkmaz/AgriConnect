package com.gri.agriconnect.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    private String category; // crops, tools, fertilizers

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

    private List<String> commentIds;
    @Size(max = 10)
    private List<String> imageLinks;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Custom constructor for mandatory fields
    public Product(String name, String supplierId) {
        this.name = name;
        this.supplierId = supplierId;
        this.favoriteCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.commentIds = new ArrayList<>();
        this.categoryTags = new ArrayList<>();
        this.imageLinks = new ArrayList<>();
    }
}
