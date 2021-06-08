package com.zing.netty.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zing
 * @date 2020-12-31
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    private Object result;

    private Throwable throwable;

}
