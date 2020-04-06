import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class Client {

    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8888;

    private AsynchronousSocketChannel client;

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            client = AsynchronousSocketChannel.open();
            Future<Void> future = client.connect(new InetSocketAddress(DEFAULT_SERVER_HOST, DEFAULT_PORT));
            future.get();

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = console.readLine();
                byte[] bytes = input.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                Future<Integer> write = client.write(buffer);
                write.get();

                buffer.flip();
                Future<Integer> read = client.read(buffer);
                read.get();

                String echo = new String(buffer.array());
                buffer.clear();
                System.out.println(echo);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            close(client);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
