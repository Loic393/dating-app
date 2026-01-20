package com.example.dating_app_a3.Chat;

import com.example.dating_app_a3.Models.User;

public class ChatMessage {
    private String text;
    private String senderId;
    private long timestamp;
    private User sender;

    // Constructor, getters, and setters

    public ChatMessage() {

    }
    public ChatMessage(String text, String senderId, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
