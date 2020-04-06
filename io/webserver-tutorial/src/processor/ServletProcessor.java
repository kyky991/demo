package processor;

import connector.*;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Zing
 * @date 2020-04-03
 */
public class ServletProcessor {

    URLClassLoader getServletLoader() throws MalformedURLException {
        File webRoot = new File(ConnectorUtils.WEB_ROOT);
        URL url = webRoot.toURI().toURL();
        return new URLClassLoader(new URL[]{url});
    }

    Servlet getServlet(URLClassLoader loader, Request request) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String uri = request.getRequestURI();
        String name = uri.substring(uri.lastIndexOf("/") + 1);

        Class<?> clazz = loader.loadClass(name);
        return (Servlet) clazz.newInstance();
    }

    public void process(Request request, Response response) throws IOException {
        URLClassLoader loader = getServletLoader();

        try {
            Servlet servlet = getServlet(loader, request);
            servlet.service(new RequestFacade(request), new ResponseFacade(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
