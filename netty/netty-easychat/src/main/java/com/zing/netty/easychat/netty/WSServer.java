package com.zing.netty.easychat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WSServer {

    private static class SingletonWSServer {
        static final WSServer INSTANCE = new WSServer();
    }

    public static WSServer getInstance() {
        return SingletonWSServer.INSTANCE;
    }

    private EventLoopGroup mainGroup;

    private EventLoopGroup subGroup;

    private ServerBootstrap serverBootstrap;

    private ChannelFuture channelFuture;

    public WSServer() {
        mainGroup = new NioEventLoopGroup();

        subGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitializer());
    }

    public void start() {
        channelFuture = serverBootstrap.bind(8088);

        System.err.println("netty websocket server 启动完毕...");
    }

}
