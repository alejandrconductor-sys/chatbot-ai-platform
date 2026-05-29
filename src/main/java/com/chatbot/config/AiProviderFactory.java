package com.chatbot.config;

import com.chatbot.ai.provider.AiProvider;
import com.chatbot.ai.provider.CloudAiProvider;
import com.chatbot.ai.provider.OllamaProvider;
import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiProviderFactory {

    private static final Logger log =
            LoggerFactory.getLogger(AiProviderFactory.class);

    private final Dotenv dotenv;

    public AiProviderFactory() {

        this.dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    public AiProvider createProvider() {

        String mode =
                dotenv.get("AI_MODE", "local").toLowerCase();

        // =========================
        // OLLAMA
        // =========================

        String ollamaUrl =
                dotenv.get("OLLAMA_BASE_URL");

        String ollamaModel =
                dotenv.get("OLLAMA_MODEL");

        // =========================
        // GROQ
        // =========================

        String groqKey =
                dotenv.get("GROQ_API_KEY");

        String groqModel =
                dotenv.get("GROQ_MODEL");

        boolean ollamaConfigured =
                ollamaUrl != null &&
                !ollamaUrl.isBlank() &&
                ollamaModel != null &&
                !ollamaModel.isBlank();

        // =========================
        // CLOUD MODE
        // =========================

        if ("cloud".equals(mode)) {

            log.info("Creating CloudAiProvider (AI_MODE=cloud)");

            return new CloudAiProvider(
                    groqKey,
                    groqModel
            );
        }

        // =========================
        // LOCAL MODE
        // =========================

        if (ollamaConfigured) {

            log.info("Creating OllamaProvider (AI_MODE=local)");

            return new OllamaProvider();
        }

        // =========================
        // SAFE FALLBACK
        // =========================

        log.warn("Ollama ENV missing -> fallback CloudAiProvider");

        return new CloudAiProvider(
                groqKey,
                groqModel
        );
    }
}
