package com.zing.user.exception;

import com.zing.common.core.domain.IErrorCode;
import com.zing.common.core.exception.ApiException;

public class UserException extends ApiException {

    public UserException(String message) {
        super(message);
    }

    public UserException(IErrorCode errorCode) {
        super(errorCode);
    }

}
