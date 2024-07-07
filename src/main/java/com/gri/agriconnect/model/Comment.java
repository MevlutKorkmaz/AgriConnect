package com.gri.agriconnect.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    @Id
    private String commentId;

    @NotBlank
    private String userId;

    @NotBlank
    private String content;

    @NotBlank
    private String postId; // ID of the post or product this comment belongs to

    private Integer likeCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Custom constructor for mandatory fields
    public Comment(String userId, String content, String postId) {
        this.userId = userId;
        this.content = content;
        this.postId = postId;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
