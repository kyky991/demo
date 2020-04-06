import connector.Connector;
import connector.NIOConnector;

/**
 * @author Zing
 * @date 2020-04-03
 */
public class Bootstrap {

    public static void main(String[] args) {
//        Connector connector = new BIOConnector();
        Connector connector = new NIOConnector();
        connector.start();
    }

}
