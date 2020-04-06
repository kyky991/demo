package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Zing
 * @date 2020-03-31
 */
public class InputHandler implements Runnable {

    private ChatClient client;

    public InputHandler(ChatClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String input = console.readLine();
                client.send(input);
                if (client.readyToQuit(input)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
