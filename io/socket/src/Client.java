import java.io.*;
import java.net.Socket;

/**
 * @author Zing
 * @date 2020-03-30
 */
public class Client {

    public static void main(String[] args) {
        final String QUIT = "quit";
        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        final int DEFAULT_PORT = 8888;

        Socket socket = null;
        BufferedWriter bw = null;

        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_PORT);

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = console.readLine();
                bw.write(input + "\n");
                bw.flush();

                String msg = br.readLine();
                System.out.println(msg);

                if (QUIT.equalsIgnoreCase(input)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
