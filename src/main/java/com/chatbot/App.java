package com.chatbot;

import com.chatbot.ai.builder.ResponseBuilder;
import com.chatbot.ai.provider.AiProvider;
import com.chatbot.ai.router.AiRouter;
import com.chatbot.config.AiProviderFactory;
import com.chatbot.config.AppConfig;
import com.chatbot.config.DatabaseConfig;
import com.chatbot.controller.ChatController;
import com.chatbot.controller.ConversationController;
import com.chatbot.controller.HealthController;
import com.chatbot.dao.ConversationDAO;
import com.chatbot.dao.MessageDAO;
import com.chatbot.service.AiService;
import com.chatbot.service.ConversationService;
import com.chatbot.service.MemoryService;
import com.chatbot.service.MessageService;
import com.chatbot.service.SummaryService;

import io.javalin.Javalin;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chatbot.ai.search.SearchProvider;
import com.chatbot.ai.search.TavilySearchProvider;
import com.chatbot.cache.SimpleCache;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        validateDatabase();

        ConversationDAO conversationDAO = new ConversationDAO();
        ConversationService conversationService = new ConversationService(conversationDAO);
        AiProviderFactory aiProviderFactory = new AiProviderFactory();
        AiProvider aiProvider = aiProviderFactory.createProvider();
        ResponseBuilder responseBuilder = new ResponseBuilder();

        SearchProvider searchProvider = new TavilySearchProvider();
        AiRouter router = new AiRouter();
        SimpleCache cache = new SimpleCache();

        MessageDAO messageDAO = new MessageDAO();
        MessageService messageService = new MessageService(messageDAO);
        SummaryService summaryService = new SummaryService(messageService);

        MemoryService memoryService = new MemoryService(messageService);

        AiService aiService = new AiService(
                aiProvider,
                responseBuilder,
                searchProvider,
                router,
                memoryService,
                cache
        );
        Javalin app = AppConfig.createApp();

        HealthController.register(app);

        ConversationController conversationController = new ConversationController(conversationService, messageService);
        conversationController.register(app);

        ChatController chatController = new ChatController(messageService, aiService, conversationService);
        chatController.register(app);

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
        app.start(port);

        log.info("Server started on http://localhost:{}", port);
    }

    private static void validateDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            log.info("Database connection established successfully");
        } catch (SQLException e) {
            log.error("Failed to connect to database: {}", e.getMessage());
            System.exit(1);
        }
    }

}
