package com.chatbot.ai.router;

public class AiRouter {

    public enum Route {
        DIRECT,     // responde sin IA (math, cosas simples)
        WEB,        // usa Tavily
        AI          // usa Ollama
    }

    public Route decide(String prompt) {

        String p = prompt.toLowerCase();

        // 🧮 Matemática directa (NO IA)
        if (p.matches(".*[0-9]+\\s*[+\\-*/].*")) {
            return Route.DIRECT;
        }

        // 🌍 Internet
        if (
            p.contains("2025") ||
            p.contains("2026") ||
            p.contains("today") ||
            p.contains("latest") ||
            p.contains("news") ||
            p.contains("president") ||
            p.contains("presidente") ||
            p.contains("hoy") ||
            p.contains("recent") ||
            p.contains("último") ||            
            p.contains("quién") ||
            p.contains("qué es") ||
            p.contains("noticia") ||
            p.contains("hoy") ||
            p.contains("actual")) {
            return Route.WEB;
        }

        // 🤖 Default IA
        return Route.AI;
    }
}
