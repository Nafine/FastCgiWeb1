package se.ifmo.validator.net;

import se.ifmo.fcgi.app.FCGIApp;
import se.ifmo.validator.net.api.Request;
import se.ifmo.validator.net.api.Response;

public class ValidatorService extends FCGIApp<Request, Response> {
    @Override
    public Response process(Request request) {
        long begin = System.nanoTime();
        return new Response(validateCoordinates(request.x(), request.y(), request.r()), System.nanoTime() - begin);
    }

    private boolean validateCoordinates(double x, double y, double r) {
        return (Math.abs(x) <= r && Math.abs(y) <= r && (
                (x >= 0 && y <= 0) ||
                        (x <= 0 && x >= -r && y <= 0 && y >= -r / 2) ||
                        (x >= 0 && y >= 0 && (x * x + y * y <= r * r)))
        );
    }

    @Override
    public Request toRequest(String httpRequest) {
        String[] query = httpRequest.split("&");
        if (query.length < 3) {
            throw new IllegalArgumentException("Invalid HTTP Request: you must provide at least 3 query parameters.");
        }

        Double x = null, y = null, r = null;
        for (String fragment : query) {
            if (fragment.length() < 3)
                throw new IllegalArgumentException("Invalid HTTP Request: you must provide value to a query parameter.");
            if (fragment.startsWith("x")) x = Double.parseDouble(fragment.substring(2));
            else if (fragment.startsWith("y")) y = Double.parseDouble(fragment.substring(2));
            else if (fragment.startsWith("r")) r = Double.parseDouble(fragment.substring(2));
        }
        if (x == null || y == null || r == null)
            throw new IllegalArgumentException("Invalid HTTP Request: you must provide x, y, and r values.");
        return new Request(x, y, r);
    }

    @Override
    public String toHTTPResponse(Response response) {
        return """
                {"hit": %b,"time": %d}
                """.formatted(response.hit(), response.execTime());
    }
}
