package client;

import java.io.*;
import java.net.Socket;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class ChatClient {

    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;

    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            bw.write(msg + "\n");
            bw.flush();
        }
    }

    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = br.readLine();
        }
        return msg;
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equalsIgnoreCase(msg);
    }

    public synchronized void close() {
        if (bw != null) {
            try {
                bw.close();
                System.out.println("Client closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_PORT);

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            new Thread(new InputHandler(this)).start();

            String msg = null;
            while ((msg = receive()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }
}
