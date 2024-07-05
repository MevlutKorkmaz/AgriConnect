package com.gri.agriconnect.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String productId;

    @NotBlank
    private String name;

    private String category; // crops, tools, fertilizers

    private String description;
    private double price;

    @NotBlank
    private String supplierId;

    private int stockQuantity;
    private int favoriteCount = 0;
    private int likeCount = 0;
    private int commentCount = 0;

    private List<String> commentIds;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
