package se.ifmo.validator.net;

import se.ifmo.fcgi.server.FCGIServer;
import se.ifmo.validator.net.api.Request;
import se.ifmo.validator.net.api.Response;

public class ValidatorService extends FCGIServer<Request, Response> {
    @Override
    public Request toRequest(String httpRequest) {
        return null;
    }

    @Override
    public String toHTTPResponse(Response response) {
        return "";
    }
}
