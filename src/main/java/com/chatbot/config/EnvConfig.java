package com.chatbot.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String get(String key) {
        String value = System.getenv(key);
        if (value != null) return value;

        return dotenv.get(key);
    }
}