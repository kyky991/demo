package com.zing.netty.rpc.server;

import com.zing.netty.rpc.codec.RpcDecoder;
import com.zing.netty.rpc.codec.RpcEncoder;
import com.zing.netty.rpc.codec.RpcRequest;
import com.zing.netty.rpc.codec.RpcResponse;
import com.zing.netty.rpc.config.provider.ProviderConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 * @date 2021-01-02
 */
@Slf4j
public class RpcServer {

    private String serverAddress;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private volatile Map<String, Object> handlerMap = new HashMap<>(16);

    public RpcServer(String serverAddress) throws InterruptedException {
        this.serverAddress = serverAddress;
        start();
    }

    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 0))
                                .addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new RpcServerHandler(handlerMap));
                    }
                });
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("server success bind to " + serverAddress);
                } else {
                    log.info("server fail bind to " + serverAddress);
                    throw new Exception("server start fail, cause: " + future.cause());
                }
            }
        });
        try {
            channelFuture.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("start rpc interrupted, ex: " + e);
        }
    }

    /**
     * 程序注册器
     */
    public void registerProcessor(ProviderConfig providerConfig) {
        handlerMap.put(providerConfig.getInterface(), providerConfig.getRef());
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
