package com.chatbot.cache;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache {

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public String get(String key) {
        return cache.get(key);
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}
