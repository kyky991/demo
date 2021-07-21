package com.zing.test.netty.ws;

import com.zing.test.netty.Constants;
import com.zing.test.netty.Server;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class ChatServer {

    public static void main(String[] args) throws Exception {
        ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        new Server()
                .childHandler(new ChatServerInitializer(channelGroup))
                .port(Constants.PORT)
                .start();
    }

}
