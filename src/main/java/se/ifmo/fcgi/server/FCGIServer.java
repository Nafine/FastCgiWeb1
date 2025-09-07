package se.ifmo.fcgi.server;

import com.fastcgi.FCGIInterface;
import se.ifmo.fcgi.HTTPAdapter.HTTPAdapter;

public abstract class FCGIServer<Request, Response> implements Runnable, HTTPAdapter<Request, Response> {
    @Override
    public void run() {
        while (new FCGIInterface().FCGIaccept() >= 0) {
            handle();
        }
    }

    private void handle() {
        Request rq = toRequest();
        Response rs = process(rq);
        String rsStr = toHTTPResponse(rs);
    }

    private Response process(Request rq) {
        return null;
    }
}
