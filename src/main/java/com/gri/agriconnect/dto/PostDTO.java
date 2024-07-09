package com.gri.agriconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostDTO {

    @NotNull(message = "User ID cannot be null")
    private String userId;

    private String title; // Allowing null value for title

    @Size(max = 2000, message = "Content can be at most 2000 characters")
    private String content;

    private List<String> categoryTags;

    private MultipartFile imageFile; // For handling the image file

    // Constructor for mandatory fields
    public PostDTO(String userId) {
        this.userId = userId;
    }
}
