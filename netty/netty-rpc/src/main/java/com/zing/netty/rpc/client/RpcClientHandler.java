package com.zing.netty.rpc.client;

import com.zing.netty.rpc.codec.RpcRequest;
import com.zing.netty.rpc.codec.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zing
 * @date 2020-12-30
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel;

    private Map<String, RpcFuture> pendingRpcTable = new ConcurrentHashMap<>(16);

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        RpcFuture rpcFuture = pendingRpcTable.get(requestId);
        if (rpcFuture != null) {
            pendingRpcTable.remove(requestId);
            rpcFuture.done(response);
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RpcFuture sendRequest(RpcRequest request) {
        RpcFuture future = new RpcFuture(request);
        pendingRpcTable.put(request.getRequestId(), future);
        channel.writeAndFlush(request);
        return future;
    }

}
