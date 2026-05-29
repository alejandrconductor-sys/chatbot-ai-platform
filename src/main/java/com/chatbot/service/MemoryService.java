package com.chatbot.service;

import com.chatbot.dao.MessageDAO;
import com.chatbot.dto.MessageDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryService {

    private final MessageService messageService;
    private static final int MAX_CONTEXT_CHARS = 1200;
    public MemoryService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String buildContext(String conversationId, int limit) {
        List<MessageDTO> messages = messageService.getMessagesByConversationId(conversationId, limit);

        if (messages == null || messages.isEmpty()) {
            return "";
        }

        List<String> validLines = new ArrayList<>();
        int currentChars = 0;

        // Iteramos asumiendo que el DAO devuelve los más recientes primero (DESC)
        for (MessageDTO msg : messages) {
            if (shouldIgnore(msg.getContent())) {
                continue;
            }

            String role = msg.getRole().equalsIgnoreCase("assistant") ? "Assistant" : "User";
            String line = role + ": " + msg.getContent() + "\n";

            if (currentChars + line.length() > MAX_CONTEXT_CHARS) {
                break; // Cortamos solo cuando ya llenamos la cuota con los más recientes
            }

            validLines.add(line);
            currentChars += line.length();
        }

        // Ahora sí invertimos solo las líneas válidas para que la lectura sea cronológica
        Collections.reverse(validLines);

        StringBuilder context = new StringBuilder();
        String topicSummary = detectTopic(messages);

        context.append("TOPIC SUMMARY:\n");
        context.append(topicSummary);
        context.append("\n\n");

        context.append("--- INICIO DE MEMORIA ---\n");
        
        for (String line : validLines) {
            context.append(line);
        }
        
        context.append("--- FIN DE MEMORIA ---\n");

        return context.toString();
    }

    private boolean shouldIgnore(String text) {
        if (text == null || text.isBlank()) {
            return true;
        }

        String normalized = text.trim().toLowerCase();

        if (normalized.length() < 3) {
            return true;
        }

        return normalized.equals("hola") ||
               normalized.equals("ok") ||
               normalized.equals("gracias") ||
               normalized.matches("^[0-9+*/().%\\s-]+$");

    }
    private String detectTopic(List<MessageDTO> messages) {

        for (MessageDTO msg : messages) {

            String text = msg.getContent().toLowerCase();

            if (text.contains("interface") || text.contains("implements")) {
                return "The user is learning Java Interfaces.";
            }

            if (text.contains("sql")) {
                return "The user is learning SQL.";
            }

            if (text.contains("spring")) {
                return "The user is learning Spring Boot.";
            }
        }

        return "";
    }
}