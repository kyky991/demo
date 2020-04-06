package server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class ChatServer {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;
    private static final int THREAD_POOL_SIZE = 8;

    private AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel server;
    private List<ClientHandler> connectedClients;
    private Charset charset = StandardCharsets.UTF_8;
    private int port;

    public ChatServer() {
        this(DEFAULT_PORT);
    }

    public ChatServer(int port) {
        this.port = port;
        this.connectedClients = new ArrayList<>();
    }

    private void start() {
        try {
            // 创建线程池
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            // 创建ChannelGroup
            group = AsynchronousChannelGroup.withThreadPool(executorService);

            // 打开管道 , 并且让管道加入我们创建的ChannelGroup
            server = AsynchronousServerSocketChannel.open(group);
            // 绑定、监听端口
            server.bind(new InetSocketAddress(DEFAULT_HOST, port));
            System.out.println("Server listen: " + port);

            while (true) {
                // 异步调用
                server.accept(null, new AcceptHandler());
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(server);
        }
    }

    private String receive(ByteBuffer buffer) {
        return String.valueOf(charset.decode(buffer));
    }

    private synchronized void addClient(ClientHandler handler) {
        // 将连接成功的用户上线
        connectedClients.add(handler);
        System.out.println(getClientName(handler.getClient()) + " connected");
    }

    private synchronized void removeClient(ClientHandler handler) {
        // 移除用户
        connectedClients.remove(handler);
        System.out.println(getClientName(handler.getClient()) + " disconnected");
        // 关闭资源
        close(handler.getClient());
    }

    private synchronized void forwardMessage(AsynchronousSocketChannel client, String message) {
        ByteBuffer buffer = null;
        for (ClientHandler handler : connectedClients) {
            AsynchronousSocketChannel channel = handler.getClient();
            if (!client.equals(channel)) {
                buffer = charset.encode(message);
                channel.write(buffer, null, handler);
                buffer.clear();
            }
        }
    }

    private String getClientName(AsynchronousSocketChannel client) {
        String name = "UNKNOWN";
        try {
            name = String.valueOf(((InetSocketAddress) client.getRemoteAddress()).getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Client[" + name + "]";
    }

    private boolean readyToQuit(String msg) {
        return QUIT.equalsIgnoreCase(msg);
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

    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

        @Override
        public void completed(AsynchronousSocketChannel client, Object attachment) {
            // 等待下一个客户端的连接
            if (server.isOpen()) {
                server.accept(null, this);
            }

            if (client != null && client.isOpen()) {
                // 为每一个用户分配一个handler，并且这个handler也相当于用户本身
                ClientHandler handler = new ClientHandler(client);
                //将用户添加到在线用户列表
                addClient(handler);

                // 创建缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER);

                /**
                 * 第一个buffer，是要写入的缓冲区。
                 * 第二个buffer，是当read完成后，
                 * 此时buffer是有数据的，
                 * 将这个buffer做为attachment。
                 * */
                // 读取客户端发送的消息
                client.read(buffer, buffer, handler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("connect failed" + exc);
        }
    }

    private class ClientHandler implements CompletionHandler<Integer, ByteBuffer> {

        private AsynchronousSocketChannel client;

        public ClientHandler(AsynchronousSocketChannel client) {
            this.client = client;
        }

        public AsynchronousSocketChannel getClient() {
            return client;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            ByteBuffer buffer = attachment;
            // 读取客户端发送的消息完成
            if (buffer != null) {
                if (result <= 0) {
                    // 将客户移除出在线客户列表
                    removeClient(this);
                } else {
                    // 读模式
                    buffer.flip();
                    String msg = receive(buffer);
                    String fwdMsg = getClientName(client) + ":" + msg;
                    System.out.println(fwdMsg);

                    // 转发消息给其他用户
                    forwardMessage(client, fwdMsg);
                    // 写模式
                    buffer.clear();

                    if (readyToQuit(msg)) {
                        // 用户退出
                        removeClient(this);
                    } else {
                        // 继续读取客户端发送的消息（一波接一波的感觉）
                        client.read(buffer, buffer, this);
                    }
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            // 先简单处理为客户端异常，移除该客户即可
            removeClient(this);
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}
