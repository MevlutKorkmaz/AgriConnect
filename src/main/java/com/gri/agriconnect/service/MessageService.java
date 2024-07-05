package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Message;
import com.gri.agriconnect.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByConversationId(String conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    public Message getMessageById(String messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
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
