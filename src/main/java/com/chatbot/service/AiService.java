package com.chatbot.service;

import com.chatbot.ai.builder.AiResponse;
import com.chatbot.ai.builder.ResponseBuilder;
import com.chatbot.ai.provider.AiProvider;
import com.chatbot.ai.router.AiRouter;
import com.chatbot.ai.search.SearchProvider;
import com.chatbot.cache.SimpleCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final AiProvider aiProvider;
    private final ResponseBuilder responseBuilder;
    private final SearchProvider searchProvider;
    private final AiRouter router;
    private final MemoryService memoryService;
    private final SimpleCache cache;

    public AiService(
            AiProvider aiProvider,
            ResponseBuilder responseBuilder,
            SearchProvider searchProvider,
            AiRouter router,
            MemoryService memoryService,
            SimpleCache cache
    ) {
        this.aiProvider = aiProvider;
        this.responseBuilder = responseBuilder;
        this.searchProvider = searchProvider;
        this.router = router;
        this.memoryService = memoryService;
        this.cache = cache;
    }

    public AiResponse generateChatResponse(String conversationId, String prompt) {

        if (prompt == null || prompt.isBlank()) {
            return responseBuilder.buildFallbackResponse("AI", "Empty prompt");
        }

        String normalizedPrompt =
                prompt.trim()
                    .toLowerCase()
                    .replaceAll("\\s+", " ");

        String cacheKey =
                conversationId + "::" +
                normalizedPrompt.toLowerCase();

        // 1. CACHE: Evitamos la doble búsqueda (contains + get)
        String cachedResult = cache.get(cacheKey);
        if (cachedResult != null) {
            log.info("Cache HIT");
            return responseBuilder.buildSuccessResponse(cachedResult, "CACHE", 0);
        }

        AiRouter.Route route = router.decide(normalizedPrompt);

        try {
            // 2. SWITCH EXPRESSION: Más limpio, seguro y sin 'break'
            String result;
            
        switch (route) {

            case DIRECT : {

                log.info("Route: DIRECT");

                result = solveDirect(normalizedPrompt);

                break;
            }

            case WEB : {

                log.info("Route: WEB + AI");

                log.info("Searching WEB for: {}", prompt);

                String webResult =
                        searchProvider.search(normalizedPrompt);

                if (webResult == null || webResult.isBlank()) {

                    log.warn("WEB SEARCH RETURNED EMPTY RESULTS");

                    webResult = "No web results found.";
                }

                log.info("WEB RESULT:\n{}", webResult);

                String memoryContext =
                        memoryService.buildContext(conversationId, 6);

                String enrichedContext =
                        memoryContext +
                        "\n\n[WEB RESULTS]\n" +
                        webResult;

                result =
                        aiProvider.generateResponse(
                                enrichedContext,
                                normalizedPrompt
                        );

                log.info("AI FINAL RESPONSE:\n{}", result);

                break;
            }

            default : {

                log.info("Route: AI ONLY");

                String memoryContext =
                        memoryService.buildContext(conversationId, 20);

                result =
                        aiProvider.generateResponse(
                                memoryContext,
                                normalizedPrompt
                        );

                break;
            }
        }

        // CACHE STORE
        if (route != AiRouter.Route.DIRECT) {

            cache.put(cacheKey, result);
        }

        return responseBuilder.buildSuccessResponse(
                result,
                aiProvider.getClass().getSimpleName(),
                estimateTokens(result)
        );

        }catch (Exception e) {
            log.error("Router execution failed: {}", e.getMessage(), e);
            return responseBuilder.buildFallbackResponse("AI", e.getMessage());
        }
    }

    // =========================
    // DIRECT MATH MODE
    // =========================

    private String solveDirect(String prompt) {


        try {
            
        if (!isMathExpression(prompt)) {
            return "Expresión matemática inválida";
        }


            double result = new net.objecthunter.exp4j.ExpressionBuilder(prompt)
                    .build()
                    .evaluate();

            return "Resultado: " + result;

        } catch (Exception e) {

            log.warn("Math solve failed: {}", e.getMessage());

            return "No se pudo resolver la operación matemática";
        }
    }   

    private boolean isMathExpression(String text) {

        return text.matches("[0-9+\\-*/().%\\s]+");
    }
 
    // =========================
    // TOKEN ESTIMATION
    // =========================

    private int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (int) Math.ceil(text.length() / 4.0);
    }
}