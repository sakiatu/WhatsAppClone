package com.beecoder.whatsapp.chat;

public class Chat {
    private String chatName;
    private String lastChatMsg;
    private String lastChatMsgDate;
    private String chatIconUrl;
    private String chatId;


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Chat(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getLastChatMsg() {
        return lastChatMsg;
    }

    public void setLastChatMsg(String lastChatMsg) {
        this.lastChatMsg = lastChatMsg;
    }

    public String getLastChatMsgDate() {
        return lastChatMsgDate;
    }

    public void setLastChatMsgDate(String lastChatMsgDate) {
        this.lastChatMsgDate = lastChatMsgDate;
    }

    public String getChatIconUrl() {
        return chatIconUrl;
    }

    public void setChatIconUrl(String chatIconUrl) {
        this.chatIconUrl = chatIconUrl;
    }
}
