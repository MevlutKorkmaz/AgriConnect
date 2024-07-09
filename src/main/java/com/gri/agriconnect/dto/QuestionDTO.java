package com.gri.agriconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class QuestionDTO {
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Body cannot be null")
    @Size(min = 220, message = "Body must be at least 220 characters")
    private String body;

    private List<String> tags;

    private MultipartFile file; // For handling the image file

    // Constructor for mandatory fields
    public QuestionDTO(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
