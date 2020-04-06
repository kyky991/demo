package connector;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Zing
 * @date 2020-04-03
 */
public abstract class Connector implements Runnable {

    private static final int DEFAULT_PORT = 8888;

    private int port;

    public Connector() {
        this(DEFAULT_PORT);
    }

    public Connector(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void start() {
        new Thread(this).start();
    }

    protected abstract void process();

    @Override
    public void run() {
        process();
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
