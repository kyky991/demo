package com.zing.bigdata.hos.core.user;

import com.zing.bigdata.hos.core.HosException;

public class HosUserException extends HosException {

    private int code;

    private String message;

    public HosUserException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public HosUserException(int code, String message) {
        super(message, null);
        this.code = code;
        this.message = message;
    }

    @Override
    public int errorCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
