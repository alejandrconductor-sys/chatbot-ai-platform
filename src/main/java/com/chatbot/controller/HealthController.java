package com.chatbot.controller;

import io.javalin.Javalin;
import java.util.Map;

public final class HealthController {

    private HealthController() {
    }

    public static void register(Javalin app) {
        app.get("/health", ctx -> ctx.json(Map.of("status", "OK")));
    }

}
