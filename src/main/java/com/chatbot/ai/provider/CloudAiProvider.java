package com.chatbot.ai.provider;

import com.chatbot.model.Conversation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.checkerframework.checker.units.qual.s;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CloudAiProvider implements AiProvider {

    private static final Logger log =
            LoggerFactory.getLogger(CloudAiProvider.class);

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private static final Duration CONNECT_TIMEOUT =
            Duration.ofSeconds(10);

    private static final Duration REQUEST_TIMEOUT =
            Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final String apiKey;
    private final String model;

    public CloudAiProvider(String apiKey, String model) {

        validateApiKey(apiKey);

        this.apiKey = apiKey;

        this.model = (model != null && !model.isBlank())
                ? model
                : "llama-3.3-70b-versatile";

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();

        this.objectMapper = new ObjectMapper();

        log.info("Groq Cloud Provider initialized with model: {}", this.model);
    }

    @Override
    public String generateResponse(String context, String prompt) {

        try {

            String requestBody =
                    buildRequestBody(context, prompt);

            HttpRequest request =
                    buildHttpRequest(requestBody);

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            validateResponse(response);

            return parseResponse(response.body());

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            log.error("Cloud AI request interrupted", e);

            return buildFriendlyError();

        } catch (IOException e) {

            log.error("Cloud AI request failed", e);

            return buildFriendlyError();

        } catch (Exception e) {

            log.error("Unexpected Cloud AI error", e);

            return buildFriendlyError();
        }
    }

    private HttpRequest buildHttpRequest(String requestBody) {

        return HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(REQUEST_TIMEOUT)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private String buildRequestBody(String context, String prompt) {

        ObjectNode rootNode =
                objectMapper.createObjectNode();

        rootNode.put("model", model);

        ArrayNode messages =
                rootNode.putArray("messages");

// Usamos String.format() y concatenación estándar para compatibilidad con Java 11
        String fullPrompt = String.format(
                "You are an intelligent conversational assistant.\n\n" +
                
                "IMPORTANT RULES:\n" +
                "1. Use the Recent Conversation to preserve context.\n" +
                "2. The user's latest message may refer to previous topics.\n" +
                "3. Never say you don't have context if Recent Conversation exists.\n" +
                "4. If WEB RESULTS are provided, use them as primary truth.\n\n" +
                
                "Recent Conversation:\n" +
                "%s\n\n" +
                
                "Current User Message:\n" +
                "%s\n", 
                context, prompt
        );
        
        ObjectNode userMessage =
                messages.addObject();

        userMessage.put("role", "user");
        userMessage.put("content", fullPrompt);

        return rootNode.toString();
    }

    private String buildPrompt(String context, String prompt) {

        return "You are a concise and helpful AI assistant.\n\n"
                + "Context:\n"
                + safe(context)
                + "\n\nUser:\n"
                + safe(prompt);
    }
   
    private String parseResponse(String responseBody)
            throws IOException {

        JsonNode json =
                objectMapper.readTree(responseBody);

        JsonNode choices =
                json.path("choices");

        if (!choices.isArray() || choices.isEmpty()) {

            log.warn(
                    "Groq returned invalid response: {}",
                    responseBody
            );

            return buildFriendlyError();
        }

        String content = choices.get(0)
                .path("message")
                .path("content")
                .asText("")
                .trim();

        if (content.isBlank()) {

            log.warn(
                    "Groq returned empty content: {}",
                    responseBody
            );

            return buildFriendlyError();
        }

        return content;
    }

    private void validateResponse(
            HttpResponse<String> response) {

        if (response.statusCode() != 200) {

            log.error(
                    "Groq error (Status {}): {}",
                    response.statusCode(),
                    response.body()
            );

            throw new RuntimeException(
                    "Groq API returned status "
                            + response.statusCode()
            );
        }
    }

    private void validateApiKey(String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {

            throw new IllegalArgumentException(
                    "GROQ_API_KEY cannot be null or empty"
            );
        }
    }

    private String safe(String text) {

        return text == null
                ? ""
                : text.trim();
    }


    private String buildFriendlyError() {

        return "I'm sorry, I'm having trouble "
                + "processing your request right now. "
                + "Please try again later.";
    }


}

