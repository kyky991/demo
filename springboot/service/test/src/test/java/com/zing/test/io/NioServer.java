package com.zing.test.io;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("OP_READ " + ((serverSocketChannel.validOps() & SelectionKey.OP_READ) != 0));
        System.out.println("OP_WRITE " + ((serverSocketChannel.validOps() & SelectionKey.OP_WRITE) != 0));
        System.out.println("OP_CONNECT " + ((serverSocketChannel.validOps() & SelectionKey.OP_CONNECT) != 0));
        System.out.println("OP_ACCEPT " + ((serverSocketChannel.validOps() & SelectionKey.OP_ACCEPT) != 0));

        System.out.println("等待连接");

        while (selector.select() > 0) {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    if (socketChannel == null) {
                        continue;
                    }
                    System.out.println("OP_READ " + ((socketChannel.validOps() & SelectionKey.OP_READ) != 0));
                    System.out.println("OP_WRITE " + ((socketChannel.validOps() & SelectionKey.OP_WRITE) != 0));
                    System.out.println("OP_CONNECT " + ((socketChannel.validOps() & SelectionKey.OP_CONNECT) != 0));
                    System.out.println("OP_ACCEPT " + ((socketChannel.validOps() & SelectionKey.OP_ACCEPT) != 0));
                    socketChannel.configureBlocking(false);

                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println(socketChannel.getRemoteAddress() + " 已连接");
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    FileOutputStream fos = new FileOutputStream(new File("service/test/src/test/resources/bean-nio-copy.xml"));
                    FileChannel fosChannel = fos.getChannel();

                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    socketChannel.read(buf);

                    buf.flip();
                    long length = buf.getLong();
                    buf.compact();
                    System.out.println("文件大小: " + length);

                    while ((socketChannel.read(buf)) != -1) {
                        buf.flip();
                        int out = 0;
                        while ((out = fosChannel.write(buf)) != 0) {
                            System.out.println("写入: " + out);
                        }
                        buf.clear();
                    }
                    fosChannel.force(true);
                    fosChannel.close();
                    fos.close();

                    key.cancel();
                }

                iterator.remove();
            }
        }

        serverSocketChannel.close();
    }

}
