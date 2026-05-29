package com.chatbot.service;

import com.chatbot.dao.MessageDAO;
import com.chatbot.dto.MessageDTO;
import com.chatbot.dto.SendMessageRequestDTO;
import com.chatbot.model.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private static final int DEFAULT_CONTEXT_LIMIT = 50;

    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public MessageDTO createMessage(SendMessageRequestDTO request) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Message message = new Message(id, request.getConversationId(), "user", request.getContent(), now);
        messageDAO.createMessage(message);

        log.info("Created message: {} for conversation: {}", id, request.getConversationId());
        return toDTO(message);
    }

    public MessageDTO createAssistantMessage(String conversationId, String content) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        Message message = new Message(id, conversationId, "assistant", content, now);
        messageDAO.createMessage(message);
        log.info("Created assistant message: {} for conversation: {}", id, conversationId);
        return toDTO(message);
    }

    public List<MessageDTO> getMessagesByConversationId(String conversationId) {
        return getMessagesByConversationId(conversationId, DEFAULT_CONTEXT_LIMIT);
    }

    public List<MessageDTO> getMessagesByConversationId(String conversationId, int limit) {
        return messageDAO.findMessagesByConversationId(conversationId, limit)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getConversationId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt().toString()
        );
    }

}
