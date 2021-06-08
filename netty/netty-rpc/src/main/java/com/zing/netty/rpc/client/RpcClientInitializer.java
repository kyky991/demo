package com.zing.netty.rpc.client;

import com.zing.netty.rpc.codec.RpcDecoder;
import com.zing.netty.rpc.codec.RpcEncoder;
import com.zing.netty.rpc.codec.RpcRequest;
import com.zing.netty.rpc.codec.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author Zing
 * @date 2020-12-30
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new RpcEncoder(RpcRequest.class))
                .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 0))
                .addLast(new RpcDecoder(RpcResponse.class))
                .addLast(new RpcClientHandler());
    }
}
