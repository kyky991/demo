import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class Server {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8888;

    private AsynchronousServerSocketChannel server;

    private void start() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
            System.out.println("Server listen: " + DEFAULT_PORT);

            while (true) {
                server.accept(null, new AcceptHandler());
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(server);
        }
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
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            if (server.isOpen()) {
                server.accept(null, this);
            }

            if (result != null && result.isOpen()) {
                ClientHandler handler = new ClientHandler(result);

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Map<String, Object> map = new HashMap<>(1);
                map.put("type", "read");
                map.put("buffer", buffer);

                result.read(buffer, map, handler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    private class ClientHandler implements CompletionHandler<Integer, Object> {

        private AsynchronousSocketChannel clientChannel;

        public ClientHandler(AsynchronousSocketChannel result) {
            this.clientChannel = result;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            Map<String, Object> map = (Map<String, Object>) attachment;
            String type = (String) map.get("type");
            if ("read".equals(type)) {
                ByteBuffer buffer = (ByteBuffer) map.get("buffer");
                buffer.flip();
                map.put("type", "write");
                clientChannel.write(buffer, map, this);
                buffer.clear();
            } else if ("write".equals(type)) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                map.put("type", "read");
                map.put("buffer", buffer);

                clientChannel.read(buffer, map, this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
