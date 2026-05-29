package com.chatbot.controller;

import com.chatbot.ai.builder.AiResponse;
import com.chatbot.dto.ApiResponseDTO;
import com.chatbot.dto.ConversationDTO;
import com.chatbot.dto.MessageDTO;
import com.chatbot.dto.SendMessageRequestDTO;
import com.chatbot.service.AiService;
import com.chatbot.service.ConversationService;
import com.chatbot.service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private static final int CONTEXT_LIMIT = 6;

    private final MessageService messageService;
    private final AiService aiService;
    private final ConversationService conversationService;

    public ChatController(MessageService messageService, AiService aiService, ConversationService conversationService) {
        this.messageService = messageService;
        this.aiService = aiService;
        this.conversationService = conversationService;
    }

    public void register(Javalin app) {
        app.post("/api/chat", this::sendMessage);
    }

    private void sendMessage(Context ctx) {
        SendMessageRequestDTO request = ctx.bodyAsClass(SendMessageRequestDTO.class);

        if (request.getConversationId() == null || request.getConversationId().isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(ApiResponseDTO.error("conversationId is required"));
            return;
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(ApiResponseDTO.error("content is required"));
            return;
        }

        Optional<ConversationDTO> conversation = conversationService.getConversationById(request.getConversationId());
        if (conversation.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(ApiResponseDTO.error("Conversation not found"));
            return;
        }

        MessageDTO userMessage = messageService.createMessage(request);
        log.info("Saved user message: {} for conversation: {}", userMessage.getId(), request.getConversationId());

        List<MessageDTO> contextMessages = messageService.getMessagesByConversationId(request.getConversationId(), CONTEXT_LIMIT);
        Collections.reverse(contextMessages);

        String context = buildContextString(contextMessages);
        String prompt = request.getContent();

        AiResponse aiResponse = aiService.generateChatResponse(
                request.getConversationId(),
                prompt
        );

        MessageDTO assistantMessage = messageService.createAssistantMessage(request.getConversationId(), aiResponse.getResponse());
        log.info("Saved assistant message: {} for conversation: {}", assistantMessage.getId(), request.getConversationId());

        ctx.json(ApiResponseDTO.success("Message sent", assistantMessage));
    }

    private String buildContextString(List<MessageDTO> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        return messages.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }
}
