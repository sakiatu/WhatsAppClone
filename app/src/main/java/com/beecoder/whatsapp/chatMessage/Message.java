package com.beecoder.whatsapp.chatMessage;

import java.util.ArrayList;

public class Message {
    private String messageId, creator, text, imageUrl;

    public Message(String messageId, String creator, String text) {
        this.messageId = messageId;
        this.creator = creator;
        this.text = text;
    }

    public Message(String messageId, String creator, String text, String imageUrl) {
        this.messageId = messageId;
        this.creator = creator;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
