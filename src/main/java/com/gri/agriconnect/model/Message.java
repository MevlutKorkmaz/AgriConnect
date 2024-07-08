package com.gri.agriconnect.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String messageId;

    @NotBlank
    @Indexed
    private String conversationId; // ID of the conversation this message belongs to

    @NotBlank
    @Indexed
    private String senderId; // ID of the user who sent the message

    @NotBlank
    @Size(max = 1000)
    private String content; // The message content

    @CreatedDate
    private LocalDateTime timestamp; // Time when the message was sent, default to current time

    private boolean isRead = false; // Message read status, default to false

    // Custom constructor for mandatory fields
    public Message(String conversationId, String senderId, String content) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Automatically set the timestamp to current time
    }
}


