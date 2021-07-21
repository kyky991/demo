package com.zing.test.netty.ws;

import com.zing.test.netty.Constants;
import com.zing.test.netty.Server;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class SecureChatServer {

    public static void main(String[] args) throws Exception {
        ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).build();
        new Server()
                .childHandler(new SecureChatServerInitializer(channelGroup, context))
                .port(Constants.PORT)
                .start();
    }

}
