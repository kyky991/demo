package com.zing.netty.easychat.enums;

/**
 * 忽略或者接受 好友请求的枚举
 */
public enum OperatorFriendRequestTypeEnum {

    IGNORE(0, "忽略"),
    ACCEPT(1, "接受");

    public final Integer type;
    public final String msg;

    OperatorFriendRequestTypeEnum(Integer type, String msg){
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public static String valueOf(Integer type) {
        for (OperatorFriendRequestTypeEnum operType : OperatorFriendRequestTypeEnum.values()) {
            if (operType.getType() == type) {
                return operType.msg;
            }
        }
        return null;
    }
}
