package com.zing.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.AttributeKey;

/**
 * 实现客户端发送一个请求，服务器会返回 hello netty
 */
public class HelloServer {

    public static void main(String[] args) throws Exception {

        // 定义一对线程组
        // 主线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        // 从线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // netty服务器的创建, ServerBootstrap 是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    // 设置主从线程组
                    .group(bossGroup, workerGroup)
                    // 设置nio的双向通道
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childAttr(AttributeKey.valueOf("key"), "value")
                    .handler(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("channelRegistered");
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("channelActive");
                        }

                        @Override
                        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("handlerAdded");
                        }
                    })
                    // 子处理器，用于处理workerGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 初始化器，channel注册后，会执行里面的相应的初始化方法
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("111111111111111111111111");
                            // 通过SocketChannel去获得对应的管道
                            ChannelPipeline pipeline = ch.pipeline();

                            // 通过管道，添加handler
                            // HttpServerCodec是由netty自己提供的助手类，可以理解为拦截器
                            // 当请求到服务端，我们需要做解码，响应到客户端做编码
                            pipeline.addLast("HttpServerCodec", new HttpServerCodec());

                            // 添加自定义的助手类，返回 "hello netty"
                            pipeline.addLast("CustomHandler", new CustomHandler());
                        }
                    });

            // 启动server，并且设置8088为启动的端口号，同时启动方式为同步
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            // 监听关闭的channel，设置位同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
