package connector;

import processor.ServletProcessor;
import processor.StaticProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Zing
 * @date 2020-04-03
 */
public class BIOConnector extends Connector {

    private ServerSocket server;

    @Override
    protected void process() {
        try {
            server = new ServerSocket(getPort());
            System.out.println("Server listen: " + getPort());

            while (true) {
                Socket socket = server.accept();
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                Request request = new Request(in);
                request.parse();

                Response response = new Response(out);
                response.setRequest(request);

                if (request.getRequestURI().startsWith("/servlet/")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                } else {
                    StaticProcessor processor = new StaticProcessor();
                    processor.process(request, response);
                }

                close(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(server);
        }
    }
}
