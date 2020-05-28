package com.beecoder.whatsapp.chatMessage;

public class Message {
    private String messageId, sender, message;


    public Message(String messageId, String sender, String message) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
