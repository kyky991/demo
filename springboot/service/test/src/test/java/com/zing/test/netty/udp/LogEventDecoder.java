package com.zing.test.netty.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf data = packet.content();
        int idx = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String logfile = data.slice(0, idx).toString(CharsetUtil.UTF_8);
        String msg = data.slice(idx + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);
        out.add(new LogEvent(packet.sender(), System.currentTimeMillis(), logfile, msg));
    }
}
