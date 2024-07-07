package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Conversation;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserService userService;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, UserService userService) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
    }

    public Conversation saveConversation(Conversation conversation) {
        // Check if both users exist
        Optional<User> senderOpt = userService.getUserById(conversation.getSenderId());
        Optional<User> receiverOpt = userService.getUserById(conversation.getReceiverId());

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            Conversation savedConversation = conversationRepository.save(conversation);
            userService.addConversationToUser(conversation.getSenderId(), savedConversation.getConversationId());
            userService.addConversationToUser(conversation.getReceiverId(), savedConversation.getConversationId());
            return savedConversation;
        } else {
            throw new IllegalArgumentException("One or both users do not exist.");
        }
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public List<Conversation> getConversationsByParticipantId(String participantId) {
        return conversationRepository.findBySenderIdOrReceiverId(participantId, participantId);
    }

    public Optional<Conversation> getConversationById(String conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public void deleteConversation(String conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            userService.removeConversationFromUser(conversation.getSenderId(), conversationId);
            userService.removeConversationFromUser(conversation.getReceiverId(), conversationId);
            conversationRepository.deleteById(conversationId);
        } else {
            throw new IllegalArgumentException("Conversation with ID " + conversationId + " does not exist.");
        }
    }

    public Conversation updateConversation(String conversationId, Conversation updatedConversation) {
        return conversationRepository.findById(conversationId).map(conversation -> {
            conversation.setSenderId(updatedConversation.getSenderId());
            conversation.setReceiverId(updatedConversation.getReceiverId());
            conversation.setMessageIds(updatedConversation.getMessageIds());
            conversation.setUpdatedAt(LocalDateTime.now());
            return conversationRepository.save(conversation);
        }).orElseGet(() -> {
            updatedConversation.setConversationId(conversationId);
            updatedConversation.setCreatedAt(LocalDateTime.now());
            updatedConversation.setUpdatedAt(LocalDateTime.now());
            return conversationRepository.save(updatedConversation);
        });
    }
}
