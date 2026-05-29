package com.chatbot.ai.search;

import com.chatbot.config.EnvConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TavilySearchProvider implements SearchProvider {

    private static final Logger log = LoggerFactory.getLogger(TavilySearchProvider.class);
    private static final String TAVILY_API_URL = "https://api.tavily.com/search";

    // Reutilizar cliente y mapper mejora enormemente el rendimiento
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public TavilySearchProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();

        this.apiKey =
                EnvConfig.get("TAVILY_API_KEY");

        log.info("TAVILY API KEY: {}", apiKey);

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("TAVILY_API_KEY missing -> WEB SEARCH DISABLED");
        }
        log.info("TAVILY KEY LOADED: {}", apiKey);
    }

    @Override
    public String search(String query) {
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }

        try {
            String jsonRequest = buildRequestBody(query);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TAVILY_API_URL))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15)) // Reducido para fallar rápido si no hay internet
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("RAW TAVILY RESPONSE:\n{}", response.body());

            return processResponse(response);

        } catch (Exception e) {
            log.error("Tavily search failed: {}", e.getMessage());
            // Lanza la excepción para que AiService active el reintento sin contexto web
            throw new RuntimeException("Error en búsqueda web: " + e.getMessage(), e);
        }
    }

    private String buildRequestBody(String query) throws Exception {
        Map<String, Object> requestBody = Map.of(
                "api_key", apiKey,
                "query", query,
                "search_depth", "basic",
                "max_results", 2 // Mantenlo en 2 para no saturar a Ollama
        );
        return objectMapper.writeValueAsString(requestBody);
    }

    private String processResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() != 200) {
            throw new RuntimeException("Tavily API error HTTP: " + response.statusCode());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode results = rootNode.path("results");
        
        if (results.isMissingNode() || !results.isArray() || results.isEmpty()) {
            return "No se encontraron resultados relevantes en internet.";
        }

        // 🔥 CRÍTICO: Transformar el JSON en texto plano para no desperdiciar tokens de Ollama
        StringBuilder formattedResults = new StringBuilder();
        for (JsonNode node : results) {
            String content = node.path("content").asText("");
            if (!content.isBlank()) {
                formattedResults.append("- ").append(content).append("\n");
            }
        }

        log.info("Tavily search success. Extracted {} relevant snippets.", results.size());
        return formattedResults.toString().trim();
    }
}