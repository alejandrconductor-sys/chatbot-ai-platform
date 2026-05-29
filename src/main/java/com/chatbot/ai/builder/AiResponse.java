package com.chatbot.ai.builder;

public class AiResponse {

    private String response;
    private String provider;
    private String timestamp;
    private int tokenEstimate;

    public AiResponse() {
    }

    public AiResponse(String response, String provider, String timestamp, int tokenEstimate) {
        this.response = response;
        this.provider = provider;
        this.timestamp = timestamp;
        this.tokenEstimate = tokenEstimate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTokenEstimate() {
        return tokenEstimate;
    }

    public void setTokenEstimate(int tokenEstimate) {
        this.tokenEstimate = tokenEstimate;
    }

}
