package com.chatbot.service;

import com.chatbot.dto.MessageDTO;
import java.util.List;

public class SummaryService {

    private final MessageService messageService;
    private String cachedSummary = null;

    public SummaryService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String getOrCreateSummary(String conversationId) {

        if (cachedSummary != null) {
            return cachedSummary;
        }

        List<MessageDTO> messages =
                messageService.getMessagesByConversationId(conversationId, 20);

        if (messages == null || messages.size() < 10) {
            return "";
        }

        StringBuilder summary = new StringBuilder();

        summary.append("User and assistant discussed about: ");

        for (MessageDTO msg : messages) {
            summary.append(msg.getContent()).append(" ");
        }

        cachedSummary = summary.toString();

        return cachedSummary;
    }
}