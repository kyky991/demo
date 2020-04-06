package client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class ChatClient {

    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private String host;
    private int port;
    private AsynchronousSocketChannel clientChannel;
    private RWHandler handler;
    private Charset charset = StandardCharsets.UTF_8;

    public ChatClient() {
        this(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
    }

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void send(String msg) {
        if (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = charset.encode(msg);
            clientChannel.write(buffer, null, handler);
        }
    }

    public boolean readyToQuit(String msg) {
        boolean flag = QUIT.equalsIgnoreCase(msg);
        if (flag) {
            close(clientChannel);
        }
        return flag;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        try {
            // 打开管道
            clientChannel = AsynchronousSocketChannel.open();
            // 异步调用connect连接服务器
            clientChannel.connect(new InetSocketAddress(host, port), null, new ConnectHandler(this));
            while (clientChannel.isOpen()) {
                // 这里没想到好方法来替代这个循坏
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(clientChannel);
        }
    }

    private class ConnectHandler implements CompletionHandler<Void, Object> {

        private ChatClient client;

        public ConnectHandler(ChatClient client) {
            this.client = client;
        }

        @Override
        public void completed(Void result, Object attachment) {
            handler = new RWHandler(clientChannel);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER);
            // 异步调用read，当服务器有消息转发给该用户，便触发回调函数
            clientChannel.read(buffer, buffer, handler);
            // 创建线程监听用户输入信息
            new Thread(new InputHandler(client)).start();
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("Client connect failed");
        }
    }

    private class RWHandler implements CompletionHandler<Integer, ByteBuffer> {

        private AsynchronousSocketChannel clientChannel;

        public RWHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            ByteBuffer buffer = attachment;
            // 读取服务器转发的消息成功
            if (buffer != null) {
                // 读取的消息有效
                if (result > 0) {
                    // 读模式
                    buffer.flip();
                    String msg = String.valueOf(charset.decode(buffer));
                    System.out.println(msg);

                    // 写模式
                    buffer.clear();
                    // 再异步调用read，相当于一直在监听服务器是否转发消息过来（异常除外）
                    clientChannel.read(buffer, buffer, this);
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            // 简单处理为客户端与服务器断开连接
            close(clientChannel);
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }

}
