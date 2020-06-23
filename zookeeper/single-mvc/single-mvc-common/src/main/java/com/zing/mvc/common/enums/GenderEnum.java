package com.zing.mvc.common.enums;

/**
 * @Description: 男女枚举
 */
public enum GenderEnum {

    MALE(0),        // 女
    FEMALE(1),        // 男
    SECRET(2);        // 保密

    public final int value;

    GenderEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
