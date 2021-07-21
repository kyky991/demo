package com.zing.test.io;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TestNIOClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(8000));

        while (socketChannel.finishConnect()) {
            Scanner scanner = new Scanner(System.in);
            String s = scanner.next();

            ByteBuffer buffer = ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

            if ("close".equals(s)) {
                socketChannel.close();
                break;
            }
        }
    }

}
