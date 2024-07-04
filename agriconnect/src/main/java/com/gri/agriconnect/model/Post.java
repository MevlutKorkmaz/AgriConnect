package com.gri.agriconnect.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post")
public class Post {
    @Id
    private String postId;

    @NotBlank
    private String userId;

    @NotBlank
    private String title;

    private String content;
    private int favoriteCount;
    private int likeCount;

    private List<String> commentIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


//{
//        "products": {
//        "_id": "ObjectId",
//        "name": "String",
//        "category": "String",  // crops, tools, fertilizers
//        "description": "String",
//        "price": "Number",
//        "supplier_id": "ObjectId",
//        "stock_quantity": "Number",
//        "created_at": "Date",
//        "updated_at": "Date"
//        }
//        }