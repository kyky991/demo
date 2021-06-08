package com.zing.netty.rpc.server;

import com.zing.netty.rpc.codec.RpcRequest;
import com.zing.netty.rpc.codec.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 * @date 2021-01-02
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private Map<String, Object> handlerMap;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 600, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65535));

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                RpcResponse response = new RpcResponse();
                response.setRequestId(request.getRequestId());
                try {
                    Object result = handle(request);
                    response.setResult(result);
                } catch (Throwable e) {
                    response.setThrowable(e);
                    log.error("rpc server handle request exception", e);
                }
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info("handle success");
                        }
                    }
                });
            }
        });
    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();

        Object serviceRef = handlerMap.get(className);
        Class<?> serviceClass = serviceRef.getClass();

        // cglib
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastClassMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastClassMethod.invoke(serviceRef, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server exception", cause);
        ctx.close();
    }
}
