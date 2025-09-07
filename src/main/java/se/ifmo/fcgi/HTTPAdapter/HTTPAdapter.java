package se.ifmo.fcgi.HTTPAdapter;

public interface HTTPAdapter<Request, Response> {
    Request toRequest(String httpRequest);
    String toHTTPResponse(Response response);
}
