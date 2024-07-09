package com.gri.agriconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private String postId; // ID of the post this comment belongs to
    private String questionId; // ID of the question this comment belongs to

    private String parentCommentId; // ID of the parent comment if this is a reply
}
