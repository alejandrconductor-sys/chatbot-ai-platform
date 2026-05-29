package com.chatbot.config;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public final class AppConfig {

    private AppConfig() {}

    public static Javalin createApp() {
        return Javalin.create(config -> {

            config.jsonMapper(new JavalinJackson());

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:5173");
                    rule.allowHost("https://chatbot-ai-platform-gamma.vercel.app");
                });
            });

        });
    }
}