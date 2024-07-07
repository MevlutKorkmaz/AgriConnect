package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Conversation;
import com.gri.agriconnect.service.ConversationService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
@Validated
@Tag(name = "Conversation", description = "Operations related to Conversations")
public class ConversationController {

    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Operation(summary = "Create a new conversation", description = "This endpoint allows you to create a new conversation.")
    @ApiResponse(responseCode = "201", description = "Conversation created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversation.class)))
    @PostMapping
    public ResponseEntity<Conversation> createConversation(@Valid @RequestBody Conversation conversation) {
        logger.info("Creating a new conversation");
        try {
            Conversation createdConversation = conversationService.saveConversation(conversation);
            return new ResponseEntity<>(createdConversation, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get all conversations", description = "Fetch all conversations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of conversations",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversation.class)))
    })
    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        logger.info("Fetching all conversations");
        List<Conversation> conversations = conversationService.getAllConversations();
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @Operation(summary = "Get conversations by participant ID", description = "Fetch all conversations for a given participant ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of conversations",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversation.class))),
            @ApiResponse(responseCode = "404", description = "No conversations found for participant ID")
    })
    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<Conversation>> getConversationsByParticipantId(
            @Parameter(description = "ID of the participant to fetch conversations for") @PathVariable String participantId) {
        logger.info("Fetching conversations for participant ID: {}", participantId);
        List<Conversation> conversations = conversationService.getConversationsByParticipantId(participantId);
        if (conversations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No conversations found for participant ID: " + participantId);
        }
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @Operation(summary = "Get a conversation by ID", description = "Fetch a conversation by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversation.class))),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @GetMapping("/{conversationId}")
    public ResponseEntity<Conversation> getConversationById(
            @Parameter(description = "ID of the conversation to fetch") @PathVariable String conversationId) {
        logger.info("Fetching conversation with ID: {}", conversationId);
        Optional<Conversation> conversation = conversationService.getConversationById(conversationId);
        return conversation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId));
    }

    @Operation(summary = "Delete a conversation by ID", description = "Delete a conversation by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conversation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @Parameter(description = "ID of the conversation to delete") @PathVariable String conversationId) {
        logger.info("Deleting conversation with ID: {}", conversationId);
        Optional<Conversation> conversation = conversationService.getConversationById(conversationId);
        if (conversation.isPresent()) {
            conversationService.deleteConversation(conversationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId);
        }
    }

    @Operation(summary = "Update a conversation", description = "Update the details of an existing conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversation.class))),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @PutMapping("/{conversationId}")
    public ResponseEntity<Conversation> updateConversation(
            @Parameter(description = "ID of the conversation to update") @PathVariable String conversationId,
            @Valid @RequestBody Conversation conversation) {
        logger.info("Updating conversation with ID: {}", conversationId);
        Optional<Conversation> existingConversation = conversationService.getConversationById(conversationId);
        if (existingConversation.isPresent()) {
            Conversation updatedConversation = conversationService.updateConversation(conversationId, conversation);
            return new ResponseEntity<>(updatedConversation, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId);
        }
    }

    @Operation(summary = "Patch a conversation", description = "Patch the details of an existing conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation patched successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Conversation.class))),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
    })
    @PatchMapping("/{conversationId}")
    public ResponseEntity<Conversation> patchConversation(
            @Parameter(description = "ID of the conversation to patch") @PathVariable String conversationId,
            @RequestBody Conversation conversation) {
        logger.info("Patching conversation with ID: {}", conversationId);
        Optional<Conversation> existingConversation = conversationService.getConversationById(conversationId);
        if (existingConversation.isPresent()) {
            Conversation updatedConversation = existingConversation.get();
            if (conversation.getSenderId() != null) {
                updatedConversation.setSenderId(conversation.getSenderId());
            }
            if (conversation.getReceiverId() != null) {
                updatedConversation.setReceiverId(conversation.getReceiverId());
            }
            if (conversation.getMessageIds() != null) {
                updatedConversation.setMessageIds(conversation.getMessageIds());
            }
            updatedConversation.setUpdatedAt(LocalDateTime.now());
            Conversation savedConversation = conversationService.saveConversation(updatedConversation);
            return new ResponseEntity<>(savedConversation, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId);
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
