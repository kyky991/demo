package com.zing.test.netty.echo;

import com.zing.test.netty.Client;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.zing.test.netty.Constants.HOST;
import static com.zing.test.netty.Constants.PORT;

public class EchoClient {

    public static void main(String[] args) throws Exception {
        new Client()
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                })
                .host(HOST)
                .port(PORT)
                .start();
    }

}
