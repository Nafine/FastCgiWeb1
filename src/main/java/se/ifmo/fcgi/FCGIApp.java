package se.ifmo.fcgi;

import com.fastcgi.FCGIInterface;
import se.ifmo.fcgi.handler.Handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FCGIApp implements Runnable {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Map<String, Map<String, Handler>> registeredHandlers = new HashMap<>();

    {
        try {
            FileHandler fh = new FileHandler("./fcgi.log", false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.addHandler(fh);
            logger.info("Logger initialized");
        } catch (SecurityException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        logger.info("Starting FCGI");
        while (new FCGIInterface().FCGIaccept() >= 0) {
            handle();
        }
    }

    public final void registerHandler(String method, String path, Handler handler) {
        registeredHandlers.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }

    private void handle() {
        logger.info("Got req from: %s".formatted(FCGIInterface.request.params.getProperty("REMOTE_ADDR")));
        try {
            String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
            logger.info("method: %s".formatted(method));
            if (!registeredHandlers.containsKey(method)) {
                logger.info("Got 405");
                System.out.println("""
                        HTTP/1.1 405 Method Not Allowed\r
                        Content-Length: 0\r
                        Allow: GET\r
                        """);
                return;
            }

            String path = FCGIInterface.request.params.getProperty("REQUEST_URI");
            logger.info("uri: %s".formatted(path));
            path = path.substring(path.lastIndexOf('/'),
                    path.indexOf('?') == -1 ? path.length() : path.indexOf('?'));
            logger.info("path: %s".formatted(path));
            if (!registeredHandlers.get(method).containsKey(path)) {
                logger.info("Got 404");
                System.out.println("""
                        HTTP/1.1 404 Not Found\r
                        Content-Length: 0\r
                        """);
                return;
            }

            registeredHandlers.get(method).get(path).process();
        } catch (Exception e) {
            logger.info("Bad req: " + e.getMessage());
            String headers = """
                    HTTP/1.1 400 Bad Request\r
                    Content-Type: application/json\r
                    Content-Length: %d\r
                    \r
                    %s""";
            String json = """
                    {"error": "Bad Request","message": "%s"}
                    """.formatted(e.getMessage());
            System.out.printf(headers, json.getBytes(StandardCharsets.UTF_8).length, json);
        }
    }

    private Map<String, String> makeHeaders() {
        Map<String, String> header = new HashMap<>();
        Properties props = FCGIInterface.request.params;
        for (String name : props.stringPropertyNames()) {
            String httpHeader = "HTTP_";
            if (name.startsWith(httpHeader)) {
                header.put(name.substring(header.size()), props.getProperty(name));
            }
        }
        return header;
    }
}
