import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Zing
 * @date 2020-03-29
 */
public class Server {

    public static void main(String[] args) {
        final String QUIT = "quit";
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Listen " + DEFAULT_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client port " + socket.getPort());

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String msg = null;
                while ((msg = br.readLine()) != null) {
                    System.out.println("Port " + socket.getPort() + ":" + msg);

                    bw.write("Server:" + msg + "\n");
                    bw.flush();

                    if (QUIT.equalsIgnoreCase(msg)) {
                        System.out.println("Client " + socket.getPort() + " exit");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
