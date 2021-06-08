package com.zing.netty.easychat.netty;

import java.io.Serializable;

public class DataContent implements Serializable {

    private static final long serialVersionUID = 6942197901466309631L;

    private Integer action;             // 动作类型
    private ChatMessage chatMessage;    // 用户的聊天内容entity
    private String extend;              // 扩展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
