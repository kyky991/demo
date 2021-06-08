package com.zing.netty.rpc.client.proxy;

import com.zing.netty.rpc.client.RpcClientHandler;
import com.zing.netty.rpc.client.RpcConnectManager;
import com.zing.netty.rpc.client.RpcFuture;
import com.zing.netty.rpc.codec.RpcRequest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 * @date 2021-01-03
 */
public class RpcProxyImpl<T> implements InvocationHandler, RpcAsyncProxy {

    private Class<T> clazz;
    private long timeout;

    public RpcProxyImpl(Class<T> clazz, long timeout) {
        this.clazz = clazz;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        // 选择合适的任务处理器
        RpcClientHandler handler = RpcConnectManager.getInstance().chooseHandler();

        // 发送真正的客户端请求 返回结果
        RpcFuture future = handler.sendRequest(request);
        return future.get(timeout, TimeUnit.SECONDS);
    }

    @Override
    public RpcFuture call(String methodName, Object... args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(clazz.getName());
        request.setMethodName(methodName);

        Class<?>[] parameterTypes = new Class[args.length];
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == args.length) {
                Parameter[] parameters = method.getParameters();
                boolean match = true;
                for (int i = 0; i < parameters.length; i++) {
                    if (!parameters[i].getType().isInstance(args[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    parameterTypes = method.getParameterTypes();
                    break;
                }
            }
        }
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);

        RpcClientHandler handler = RpcConnectManager.getInstance().chooseHandler();
        return handler.sendRequest(request);
    }

}
