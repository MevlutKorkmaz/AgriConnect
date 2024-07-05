package com.gri.agriconnect.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String messageId;

    @NotBlank
    private String conversationId; // ID of the conversation this message belongs to

    @NotBlank
    private String senderId; // ID of the user who sent the message

    @NotBlank
    private String content; // The message content

    private LocalDateTime timestamp; // Time when the message was sent

    private boolean isRead; // Message read status
}

