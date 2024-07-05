package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Conversation;
import com.gri.agriconnect.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation saveConversation(Conversation conversation) {
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public List<Conversation> getConversationsByParticipantId(String participantId) {
        return conversationRepository.findByParticipantIdsContaining(participantId);
    }

    public Optional<Conversation> getConversationById(String conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public void deleteConversation(String conversationId) {
        conversationRepository.deleteById(conversationId);
    }

    public Conversation updateConversation(String conversationId, Conversation updatedConversation) {
        return conversationRepository.findById(conversationId).map(conversation -> {
            conversation.setName(updatedConversation.getName());
            conversation.setParticipantIds(updatedConversation.getParticipantIds());
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
