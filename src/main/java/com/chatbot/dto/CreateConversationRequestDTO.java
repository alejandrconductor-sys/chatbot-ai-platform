package com.chatbot.dto;

public class CreateConversationRequestDTO {

    private String title;

    public CreateConversationRequestDTO() {
    }

    public CreateConversationRequestDTO(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
