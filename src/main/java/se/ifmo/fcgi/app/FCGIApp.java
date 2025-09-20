package se.ifmo.fcgi.app;

import com.fastcgi.FCGIInterface;
import se.ifmo.fcgi.http.HTTPAdapter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public abstract class FCGIApp<Request, Response> implements Runnable, HTTPAdapter<Request, Response> {
    private final Set<String> allowedMethods = new HashSet<>();

    @Override
    public void run() {
        while (new FCGIInterface().FCGIaccept() >= 0) {
            handle();
        }
    }

    protected void registerMethod(String method) {
        allowedMethods.add(method);
    }

    private void handle() {
        try {
            if (!allowedMethods.contains(FCGIInterface.request.params.getProperty("REQUEST_METHOD"))) {
                System.out.println("""
                        HTTP/1.1 405 Method Not Allowed\r
                        Content-Length: 0\r
                        Allow: GET\r
                        """);
                return;
            }

            Request rq = toRequest(FCGIInterface.request.params.getProperty("QUERY_STRING"));
            Response rs = process(rq);
            String rsStr = toHTTPResponse(rs);
            String headers = """
                    HTTP/1.1 200 OK\r
                    Content-Type: application/json\r
                    Content-Length: %d\r
                    \r
                    %s""";
            System.out.printf(headers, rsStr.getBytes(StandardCharsets.UTF_8).length, rsStr);
        } catch (Exception e) {
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

    protected abstract Response process(Request rq);
}
