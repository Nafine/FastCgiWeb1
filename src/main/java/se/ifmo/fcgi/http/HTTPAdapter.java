package se.ifmo.fcgi.http;

import se.ifmo.fcgi.http.exception.ParseException;

public interface HTTPAdapter<Request, Response> {
    Request toRequest(String httpRequest) throws ParseException;

    String toHTTPResponse(Response response);
}
