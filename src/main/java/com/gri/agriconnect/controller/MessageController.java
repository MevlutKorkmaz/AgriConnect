package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Message;
import com.gri.agriconnect.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Validated
@Tag(name = "Message", description = "Operations related to Messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Create a new message", description = "This endpoint allows you to create a new message.")
    @ApiResponse(responseCode = "201", description = "Message created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)))
    @PostMapping
    public ResponseEntity<Message> createMessage(@Valid @RequestBody Message message) {
        logger.info("Creating a new message");
        try {
            Message createdMessage = messageService.saveMessage(message);
            return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get messages by conversation ID", description = "Fetch all messages for a given conversation ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of messages",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "No messages found for conversation ID")
    })
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message>> getMessagesByConversationId(
            @Parameter(description = "ID of the conversation to fetch messages for") @PathVariable String conversationId) {
        logger.info("Fetching messages for conversation ID: {}", conversationId);
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        if (messages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No messages found for conversation ID: " + conversationId);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @Operation(summary = "Get unread messages by conversation ID", description = "Fetch all unread messages for a given conversation ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of unread messages",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "No unread messages found for conversation ID")
    })
    @GetMapping("/conversation/{conversationId}/unread")
    public ResponseEntity<List<Message>> getUnreadMessagesByConversationId(
            @Parameter(description = "ID of the conversation to fetch unread messages for") @PathVariable String conversationId) {
        logger.info("Fetching unread messages for conversation ID: {}", conversationId);
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        List<Message> unreadMessages = messages.stream().filter(message -> !message.isRead()).toList();
        if (unreadMessages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No unread messages found for conversation ID: " + conversationId);
        }
        return new ResponseEntity<>(unreadMessages, HttpStatus.OK);
    }

    @Operation(summary = "Get a message by ID", description = "Fetch a message by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(
            @Parameter(description = "ID of the message to fetch") @PathVariable String messageId) {
        logger.info("Fetching message with ID: {}", messageId);
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Operation(summary = "Delete a message by ID", description = "Delete a message by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "ID of the message to delete") @PathVariable String messageId) {
        logger.info("Deleting message with ID: {}", messageId);
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            messageService.deleteMessage(messageId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @Operation(summary = "Update a message", description = "Update the details of an existing message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(
            @Parameter(description = "ID of the message to update") @PathVariable String messageId,
            @Valid @RequestBody Message message) {
        logger.info("Updating message with ID: {}", messageId);
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage != null) {
            Message updatedMessage = messageService.updateMessage(messageId, message);
            return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @Operation(summary = "Patch a message", description = "Patch the details of an existing message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message patched successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> patchMessage(
            @Parameter(description = "ID of the message to patch") @PathVariable String messageId,
            @RequestBody Message message) {
        logger.info("Patching message with ID: {}", messageId);
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage != null) {
            if (message.getContent() != null) {
                existingMessage.setContent(message.getContent());
            }
            if (message.isRead() != existingMessage.isRead()) {
                existingMessage.setRead(message.isRead());
            }
            // Add more fields to patch as needed
            Message savedMessage = messageService.saveMessage(existingMessage);
            return new ResponseEntity<>(savedMessage, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @Operation(summary = "Mark a message as read", description = "Mark a message as read by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message marked as read successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Message> markMessageAsRead(
            @Parameter(description = "ID of the message to mark as read") @PathVariable String messageId) {
        logger.info("Marking message as read with ID: {}", messageId);
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage != null) {
            existingMessage.setRead(true);
            Message savedMessage = messageService.saveMessage(existingMessage);
            return new ResponseEntity<>(savedMessage, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
