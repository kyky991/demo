package processor;

import connector.Request;
import connector.Response;

import java.io.IOException;

/**
 * @author Zing
 * @date 2020-04-03
 */
public class StaticProcessor {

    public void process(Request request, Response response) {
        try {
            response.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
