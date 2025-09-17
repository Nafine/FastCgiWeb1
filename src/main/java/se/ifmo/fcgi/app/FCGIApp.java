package se.ifmo.fcgi.app;

import com.fastcgi.FCGIInterface;
import se.ifmo.fcgi.http.HTTPAdapter;

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
                System.out.println("Method not allowed.");
            }

            Request rq = toRequest(FCGIInterface.request.params.getProperty("QUERY_STRING"));
            Response rs = process(rq);
            String rsStr = toHTTPResponse(rs);
            String headers = """
                    HTTP/1.1 200 OK
                    Content-Type: application/json
                    Content-Length: %d
                    
                    %s
                    
                    """;
            System.out.printf(headers, rsStr.getBytes(StandardCharsets.UTF_8).length, rsStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    protected abstract Response process(Request rq);
}
