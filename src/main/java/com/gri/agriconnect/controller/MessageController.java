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

@RestController
@RequestMapping("/api/messages")
@Validated
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

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

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message>> getMessagesByConversationId(@PathVariable String conversationId) {
        logger.info("Fetching messages for conversation ID: {}", conversationId);
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        if (messages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No messages found for conversation ID: " + conversationId);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/conversation/{conversationId}/unread")
    public ResponseEntity<List<Message>> getUnreadMessagesByConversationId(@PathVariable String conversationId) {
        logger.info("Fetching unread messages for conversation ID: {}", conversationId);
        List<Message> messages = messageService.getMessagesByConversationId(conversationId);
        List<Message> unreadMessages = messages.stream().filter(message -> !message.isRead()).toList();
        if (unreadMessages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No unread messages found for conversation ID: " + conversationId);
        }
        return new ResponseEntity<>(unreadMessages, HttpStatus.OK);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable String messageId) {
        logger.info("Fetching message with ID: {}", messageId);
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Message>> getMessagesByUserId(@PathVariable String userId) {
//        logger.info("Fetching messages for user ID: {}", userId);
//        List<Message> messages = messageService.getMessagesByUserId(userId);
//        if (messages.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No messages found for user ID: " + userId);
//        }
//        return new ResponseEntity<>(messages, HttpStatus.OK);
//    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        logger.info("Deleting message with ID: {}", messageId);
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            messageService.deleteMessage(messageId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable String messageId, @Valid @RequestBody Message message) {
        logger.info("Updating message with ID: {}", messageId);
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage != null) {
            Message updatedMessage = messageService.updateMessage(messageId, message);
            return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found with ID: " + messageId);
        }
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> patchMessage(@PathVariable String messageId, @RequestBody Message message) {
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

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Message> markMessageAsRead(@PathVariable String messageId) {
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
