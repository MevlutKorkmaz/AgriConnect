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

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@Validated
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> createMessage(@Valid @RequestBody Message message) {
        logger.info("Creating a new message");
        Message createdMessage = messageService.saveMessage(message);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message>> getMessagesByConversationId(@PathVariable String conversationId) {
        logger.info("Fetching messages for conversation ID: {}", conversationId);
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        if (messages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No messages found for conversation ID: " + conversationId);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable String messageId) {
        logger.info("Fetching message with ID: {}", messageId);
        Optional<Message> message = Optional.ofNullable(messageService.getMessageById(messageId));
        return message.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        logger.info("Deleting message with ID: {}", messageId);
        Optional<Message> message = Optional.ofNullable(messageService.getMessageById(messageId));
        if (message.isPresent()) {
            messageService.deleteMessage(messageId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable String messageId, @Valid @RequestBody Message message) {
        logger.info("Updating message with ID: {}", messageId);
        Optional<Message> existingMessage = Optional.ofNullable(messageService.getMessageById(messageId));
        if (existingMessage.isPresent()) {
            Message updatedMessage = messageService.updateMessage(messageId, message);
            return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> patchMessage(@PathVariable String messageId, @RequestBody Message message) {
        logger.info("Patching message with ID: {}", messageId);
        Optional<Message> existingMessage = Optional.ofNullable(messageService.getMessageById(messageId));
        if (existingMessage.isPresent()) {
            Message updatedMessage = existingMessage.get();
            if (message.getContent() != null) {
                updatedMessage.setContent(message.getContent());
            }
            if (message.isRead() != updatedMessage.isRead()) {
                updatedMessage.setRead(message.isRead());
            }
            // Add more fields to patch as needed
            Message savedMessage = messageService.saveMessage(updatedMessage);
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
