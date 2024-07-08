package com.gri.agriconnect.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String conversationId;

    @NotBlank
    @Indexed
    private String senderId;

    @NotBlank
    @Indexed
    private String receiverId;

    private List<String> messageIds = new ArrayList<>(); // Initialize the list to avoid null pointer exceptions

    @CreatedDate
    private LocalDateTime createdAt; // Track when the conversation was created

    @LastModifiedDate
    private LocalDateTime updatedAt; // Track when the conversation was last updated

    // Custom constructor for mandatory fields
    public Conversation(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = LocalDateTime.now(); // Automatically set the creation timestamp
        this.updatedAt = LocalDateTime.now(); // Automatically set the last modified timestamp
    }
}


