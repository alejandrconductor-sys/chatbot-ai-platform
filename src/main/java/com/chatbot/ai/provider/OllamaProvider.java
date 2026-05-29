package com.chatbot.ai.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OllamaProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(OllamaProvider.class);

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "llama3.2:3b";
    
    // Limites optimizados para una laptop personal
    private static final int MAX_CONTEXT_LENGTH = 600;
    private static final int OLLAMA_TIMEOUT_SECONDS = 90; // Menor tiempo para fallar rápido
    private static final int LOCAL_CONNECT_TIMEOUT = 5;   // Conexión local debería ser inmediata

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String model;

    public OllamaProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(LOCAL_CONNECT_TIMEOUT))
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = System.getenv("OLLAMA_BASE_URL");
        this.model = System.getenv("OLLAMA_MODEL");
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("OLLAMA_BASE_URL no configurado");
        }

        if (model == null || model.isBlank()) {
            throw new IllegalStateException("OLLAMA_MODEL no configurado");
        }
        log.info("Ollama initialized -> model: {}, baseUrl: {}", model, baseUrl);
    }

    @Override
    public String generateResponse(String context, String prompt) {
        log.info("Ollama request started - model: {}, endpoint: {}/api/generate", model, baseUrl);
        
        try {
            String fullPrompt = buildOptimizedPrompt(context, prompt);
            String jsonRequest = buildRequestBody(fullPrompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/generate"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(OLLAMA_TIMEOUT_SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return processResponse(httpResponse, fullPrompt.length());

        } catch (HttpTimeoutException e) {
            log.warn("Ollama request timeout: {}", e.getMessage());
            // 🔥 CRÍTICO: Lanzar excepción para que AiService active el Fallback (reintento sin web search)
            throw new RuntimeException("Ollama Timeout: El modelo tardó demasiado", e);
            
        } catch (ConnectException e) {
            log.error("Ollama connection refused on {}: {}", baseUrl, e.getMessage());
            throw new RuntimeException("Servicio de IA local no disponible. Verifica que Ollama esté en ejecución.", e);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ollama request interrupted");
            throw new RuntimeException("Petición interrumpida", e);
            
        } catch (IOException e) {
            log.error("Ollama request failed: {}", e.getMessage());
            throw new RuntimeException("Fallo en la comunicación con la IA local", e);
        }
    }

    private String buildOptimizedPrompt(String context, String prompt) {
        String trimmedContext = context == null ? "" : context;

        // Truncado más seguro
        if (trimmedContext.length() > MAX_CONTEXT_LENGTH) {
            trimmedContext = trimmedContext.substring(trimmedContext.length() - MAX_CONTEXT_LENGTH);
        }

        return "You are a concise and helpful AI assistant.\n" +
               "Respond clearly and briefly.\n\n" +
               "Conversation:\n" +
               trimmedContext +
               "\n\nUser: " +
               prompt +
               "\n\nAssistant:";
    }

    private String buildRequestBody(String fullPrompt) throws JsonProcessingException {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", fullPrompt,
                "stream", false,
                "options", Map.of(
                        "num_ctx", 512, 
                        "temperature", 0.5,
                        "num_predict", 80,
                        "top_k", 20,
                        "top_p", 0.9
                )
        );
        log.info("Prompt length: {}", fullPrompt.length());
        return objectMapper.writeValueAsString(requestBody);
    }

    private String processResponse(HttpResponse<String> httpResponse, int promptLength) throws JsonProcessingException {
        if (httpResponse.statusCode() != 200) {
            String errorMsg = "Ollama returned non-200 status: " + httpResponse.statusCode();
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        String responseBody = httpResponse.body();
        if (responseBody == null || responseBody.isBlank()) {
            throw new RuntimeException("Ollama returned empty response");
        }

        // Usar JsonNode para una lectura más segura y limpia
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        if (jsonResponse.has("error")) {
            String error = jsonResponse.get("error").asText();
            log.error("Ollama returned error: {}", error);
            throw new RuntimeException("Error interno de Ollama: " + error);
        }

        if (!jsonResponse.has("response")) {
            log.error("Ollama response missing 'response' field");
            throw new RuntimeException("Respuesta de IA malformada");
        }

        String result = jsonResponse.get("response").asText().trim();
        log.info("Ollama response generated - Prompt: {} chars, Response: {} chars", promptLength, result.length());
        
        return result;
    }
}