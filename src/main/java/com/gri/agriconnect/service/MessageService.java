package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Conversation;
import com.gri.agriconnect.model.Message;
import com.gri.agriconnect.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    @Autowired
    public MessageService(MessageRepository messageRepository, ConversationService conversationService) {
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
    }

    public Message saveMessage(Message message) {
        Optional<Conversation> conversationOpt = conversationService.getConversationById(message.getConversationId());
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            Message savedMessage = messageRepository.save(message);
            conversation.getMessageIds().add(savedMessage.getMessageId());
            conversationService.updateConversation(conversation.getConversationId(), conversation);
            return savedMessage;
        } else {
            throw new IllegalArgumentException("Conversation with ID " + message.getConversationId() + " does not exist.");
        }
    }

    public List<Message> getMessagesByConversationId(String conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    public Message getMessageById(String messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    public void deleteMessage(String messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            Optional<Conversation> conversationOpt = conversationService.getConversationById(message.getConversationId());
            if (conversationOpt.isPresent()) {
                Conversation conversation = conversationOpt.get();
                conversation.getMessageIds().remove(messageId);
                conversationService.updateConversation(conversation.getConversationId(), conversation);
            }
            messageRepository.deleteById(messageId);
        } else {
            throw new IllegalArgumentException("Message with ID " + messageId + " does not exist.");
        }
    }

    public Message updateMessage(String messageId, Message updatedMessage) {
        return messageRepository.findById(messageId).map(message -> {
            message.setContent(updatedMessage.getContent());
            message.setRead(updatedMessage.isRead());
            return messageRepository.save(message);
        }).orElseGet(() -> {
            updatedMessage.setMessageId(messageId);
            return messageRepository.save(updatedMessage);
        });
    }
}

