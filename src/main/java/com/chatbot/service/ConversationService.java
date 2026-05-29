package com.chatbot.service;

import com.chatbot.dao.ConversationDAO;
import com.chatbot.dto.ConversationDTO;
import com.chatbot.dto.CreateConversationRequestDTO;
import com.chatbot.model.Conversation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationDAO conversationDAO;

    public ConversationService(ConversationDAO conversationDAO) {
        this.conversationDAO = conversationDAO;
    }

    public ConversationDTO createConversation(CreateConversationRequestDTO request) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Conversation conversation = new Conversation(id, request.getTitle(), now, now);
        conversationDAO.createConversation(conversation);

        log.info("Created conversation: {}", id);
        return toDTO(conversation);
    }

    public List<ConversationDTO> listConversations() {
        return conversationDAO.findAllConversations()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ConversationDTO> getConversationById(String id) {
        return conversationDAO.findConversationById(id)
                .map(this::toDTO);
    }

    private ConversationDTO toDTO(Conversation conversation) {
        return new ConversationDTO(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt().toString(),
                conversation.getUpdatedAt().toString()
        );
    }

}
