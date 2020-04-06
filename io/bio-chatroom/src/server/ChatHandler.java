package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class ChatHandler implements Runnable {

    private ChatServer server;
    private Socket socket;

    public ChatHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            server.addClient(socket);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String msg = null;
            while ((msg = br.readLine()) != null) {
                String fwd = "Client[" + socket.getPort() + "]: " + msg + "\n";
                System.out.println(fwd);

                server.forwardMessage(socket, fwd);
                if (server.readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
