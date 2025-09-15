package se.ifmo.fcgi.app;

import com.fastcgi.FCGIInterface;
import se.ifmo.fcgi.http.HTTPAdapter;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FCGIApp<Request, Response> implements Runnable, HTTPAdapter<Request, Response> {
    @Override
    public void run() {
        while (new FCGIInterface().FCGIaccept() >= 0) {
            handle();
        }
    }

    private void handle() {
        try {
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
