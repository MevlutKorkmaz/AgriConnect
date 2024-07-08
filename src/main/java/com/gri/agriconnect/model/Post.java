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

    private List<String> commentIds;
    @Size(max = 10)
    private List<String> imageLinks;

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
        this.imageLinks = new ArrayList<>();
    }
}
