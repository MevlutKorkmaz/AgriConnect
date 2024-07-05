package com.gri.agriconnect.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "comments")
public class Comment {
    @Id
    private String commendId;

    @NotBlank
    private String userId;

    @NotBlank
    private String content;

    private int likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}