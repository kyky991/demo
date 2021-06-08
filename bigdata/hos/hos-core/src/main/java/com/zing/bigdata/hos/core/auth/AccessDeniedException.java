package com.zing.bigdata.hos.core.auth;

import com.zing.bigdata.hos.core.ErrorCode;
import com.zing.bigdata.hos.core.HosException;

public class AccessDeniedException extends HosException {

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(String resourcePath, long userId, String accessType) {
        super(String.format("access denied:%d->%s,%s", userId, resourcePath, accessType), null);
    }

    public AccessDeniedException(String resPath, long userId) {
        super(String.format("access denied:%d->%s not owner", userId, resPath), null);
    }

    @Override
    public int errorCode() {
        return ErrorCode.ERROR_PERMISSION_DENIED;
    }
}
