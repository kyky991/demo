package com.zing.test.netty.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent event) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append(event.getReceived())
                .append(" [")
                .append(event.getSource().toString())
                .append("] [")
                .append(event.getLogfile())
                .append("] : ")
                .append(event.getMsg());
        System.out.println(sb.toString());
    }
}
