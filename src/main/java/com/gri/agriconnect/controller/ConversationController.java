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
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
@Validated
public class ConversationController {

    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<Conversation> createConversation(@Valid @RequestBody Conversation conversation) {
        logger.info("Creating a new conversation");
        Conversation createdConversation = conversationService.saveConversation(conversation);
        return new ResponseEntity<>(createdConversation, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        logger.info("Fetching all conversations");
        List<Conversation> conversations = conversationService.getAllConversations();
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<Conversation>> getConversationsByParticipantId(@PathVariable String participantId) {
        logger.info("Fetching conversations for participant ID: {}", participantId);
        List<Conversation> conversations = conversationService.getConversationsByParticipantId(participantId);
        if (conversations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No conversations found for participant ID: " + participantId);
        }
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable String conversationId) {
        logger.info("Fetching conversation with ID: {}", conversationId);
        Optional<Conversation> conversation = conversationService.getConversationById(conversationId);
        return conversation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId));
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String conversationId) {
        logger.info("Deleting conversation with ID: {}", conversationId);
        Optional<Conversation> conversation = conversationService.getConversationById(conversationId);
        if (conversation.isPresent()) {
            conversationService.deleteConversation(conversationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId);
        }
    }

    @PutMapping("/{conversationId}")
    public ResponseEntity<Conversation> updateConversation(@PathVariable String conversationId, @Valid @RequestBody Conversation conversation) {
        logger.info("Updating conversation with ID: {}", conversationId);
        Optional<Conversation> existingConversation = conversationService.getConversationById(conversationId);
        if (existingConversation.isPresent()) {
            Conversation updatedConversation = conversationService.updateConversation(conversationId, conversation);
            return new ResponseEntity<>(updatedConversation, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found with ID: " + conversationId);
        }
    }

    @PatchMapping("/{conversationId}")
    public ResponseEntity<Conversation> patchConversation(@PathVariable String conversationId, @RequestBody Conversation conversation) {
        logger.info("Patching conversation with ID: {}", conversationId);
        Optional<Conversation> existingConversation = conversationService.getConversationById(conversationId);
        if (existingConversation.isPresent()) {
            Conversation updatedConversation = existingConversation.get();
            if (conversation.getName() != null) {
                updatedConversation.setName(conversation.getName());
            }
            if (conversation.getParticipantIds() != null) {
                updatedConversation.setParticipantIds(conversation.getParticipantIds());
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
