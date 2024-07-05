package com.gri.agriconnect.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "conversations")
public class Conversation {
    @Id
    private String conversationId;

    @NotBlank
    private String name; // Optional, can be used for group chats

    private List<String> participantIds; // List of user IDs participating in the conversation

    private List<String> messageIds; // List of message IDs in the conversation

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

