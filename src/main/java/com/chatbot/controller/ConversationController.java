package com.chatbot.controller;

import com.chatbot.dto.ApiResponseDTO;
import com.chatbot.dto.ConversationDTO;
import com.chatbot.dto.CreateConversationRequestDTO;
import com.chatbot.dto.MessageDTO;
import com.chatbot.service.ConversationService;
import com.chatbot.service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationController {

    private static final Logger log = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    public void register(Javalin app) {
        app.get("/api/conversations", this::listConversations);
        app.post("/api/conversations", this::createConversation);
        app.get("/api/conversations/{id}/messages", this::getMessages);
    }

    private void listConversations(Context ctx) {
        List<ConversationDTO> conversations = conversationService.listConversations();
        ctx.json(ApiResponseDTO.success(conversations));
    }

    private void createConversation(Context ctx) {
        CreateConversationRequestDTO request = ctx.bodyAsClass(CreateConversationRequestDTO.class);
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(ApiResponseDTO.error("Title is required"));
            return;
        }
        ConversationDTO created = conversationService.createConversation(request);
        ctx.status(HttpStatus.CREATED);
        ctx.json(ApiResponseDTO.success("Conversation created", created));
    }

    private void getMessages(Context ctx) {
        String id = ctx.pathParam("id");
        log.debug("Fetching messages for conversation: {}", id);
        List<MessageDTO> messages = messageService.getMessagesByConversationId(id);
        ctx.json(ApiResponseDTO.success(messages));
    }
}
