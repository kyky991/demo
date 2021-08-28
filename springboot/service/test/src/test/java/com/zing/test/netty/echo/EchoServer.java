package com.zing.test.netty.echo;

import com.zing.test.netty.Server;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.zing.test.netty.Constants.PORT;

public class EchoServer {

    public static void main(String[] args) throws Exception {
        EchoServerHandler serverHandler = new EchoServerHandler();

        // 此方式只会使用一个线程去处理，适合客户端连接多、业务处理时间短的场景
        NioEventLoopGroup group = new NioEventLoopGroup();

        new Server()
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(group, serverHandler);
                    }
                })
                .port(PORT)
                .start();
    }

}
