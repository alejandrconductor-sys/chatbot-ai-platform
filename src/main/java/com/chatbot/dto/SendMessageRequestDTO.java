package com.chatbot.dto;

public class SendMessageRequestDTO {

    private String conversationId;
    private String content;

    public SendMessageRequestDTO() {
    }

    public SendMessageRequestDTO(String conversationId, String content) {
        this.conversationId = conversationId;
        this.content = content;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
