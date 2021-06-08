package com.zing.netty.easychat.netty;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 8850170151773060862L;

    private String senderId;        // 发送者的用户id
    private String receiverId;      // 接受者的用户id
    private String message;         // 聊天内容
    private String messageId;       // 用于消息的签收

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
