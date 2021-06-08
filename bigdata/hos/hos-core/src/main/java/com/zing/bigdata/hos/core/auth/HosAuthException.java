package com.zing.bigdata.hos.core.auth;

import com.zing.bigdata.hos.core.HosException;

public class HosAuthException extends HosException {

    private int code;

    private String message;

    public HosAuthException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public HosAuthException(int code, String message) {
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
