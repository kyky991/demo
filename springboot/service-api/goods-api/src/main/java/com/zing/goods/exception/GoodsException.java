package com.zing.goods.exception;

import com.zing.common.core.domain.IErrorCode;
import com.zing.common.core.exception.ApiException;

public class GoodsException extends ApiException {

    public GoodsException(String message) {
        super(message);
    }

    public GoodsException(IErrorCode errorCode) {
        super(errorCode);
    }

}
