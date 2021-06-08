package com.zing.netty.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zing
 * @date 2020-12-31
 */
@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

}
