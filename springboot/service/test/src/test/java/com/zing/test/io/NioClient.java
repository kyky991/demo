package com.zing.test.io;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NioClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));

        while (!socketChannel.finishConnect()) {
            Thread.sleep(500);
            System.out.println("连接中...");
        }

        System.out.println("开始传输文件");

        File file = new File("service/test/src/test/resources/bean.xml");
        FileInputStream fis = new FileInputStream(file);
        FileChannel fisChannel = fis.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.putLong(file.length());
        buf.flip();
        socketChannel.write(buf);
        buf.clear();

        while ((fisChannel.read(buf)) != -1) {
            buf.flip();
            int out = 0;
            while ((out = socketChannel.write(buf)) != 0) {
                System.out.println("写入: " + out);
            }
            buf.clear();
        }
        fisChannel.force(true);
        fisChannel.close();
        fis.close();

        socketChannel.close();
    }

}
