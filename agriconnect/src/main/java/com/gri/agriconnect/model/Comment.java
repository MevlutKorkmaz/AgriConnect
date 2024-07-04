package com.gri.agriconnect.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;

    @NotBlank
    private String userId;

    @NotBlank
    private String content;

    private int favoriteCount;
    private int likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
