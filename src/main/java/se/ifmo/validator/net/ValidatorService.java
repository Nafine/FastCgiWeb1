package se.ifmo.validator.net;

import com.fastcgi.FCGIInterface;
import se.ifmo.fcgi.FCGIApp;
import se.ifmo.validator.net.api.dots.Dot;
import se.ifmo.validator.net.api.dots.DotsRequest;
import se.ifmo.validator.net.api.dots.DotsResponse;
import se.ifmo.validator.net.api.hit.HitRequest;
import se.ifmo.validator.net.api.hit.HitResponse;
import se.ifmo.validator.net.parse.ParseException;
import se.ifmo.validator.net.parse.QueryParser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class ValidatorService implements Runnable {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final FCGIApp fcgiApp;

    private final LinkedHashMap<HitRequest, HitResponse> cache = new LinkedHashMap<>();

    public ValidatorService() {
        fcgiApp = new FCGIApp();

        fcgiApp.registerHandler("GET", "/", this::processHit);
        fcgiApp.registerHandler("GET", "/dots", this::processDots);
    }

    @Override
    public void run() {
        fcgiApp.run();
    }

    private void processHit() throws ParseException {
        long begin = System.nanoTime();
        HitRequest req = parseHitRequest();

        cache.computeIfAbsent(req, k -> new HitResponse(validateCoordinates(req.x(), req.y(), req.r()),
                LocalDateTime.now(), System.nanoTime() - begin));

        HitResponse resp = cache.get(req);
        writeHitResponse(resp);
        if (cache.size() > 10000) {
            cache.remove(cache.lastEntry().getKey());
        }
        logger.info("Cache size: " + cache.size());
    }

    private HitRequest parseHitRequest() throws ParseException {
        Map<String, String> queries = QueryParser.parse(FCGIInterface.request.params.getProperty("QUERY_STRING"));
        if (queries.size() < 3) {
            throw new ParseException("Invalid query string: must provide at least 3 parameters.");
        }
        Double x = null, y = null, r = null;
        for (String key : queries.keySet()) {
            if (key.equals("x")) x = Double.valueOf(queries.get(key));
            if (key.equals("y")) y = Double.valueOf(queries.get(key));
            if (key.equals("r")) r = Double.valueOf(queries.get(key));
        }
        if (x == null || y == null || r == null) {
            throw new ParseException("Invalid query string: must provide x, y and r.");
        }
        return new HitRequest(x, y, r);
    }

    private boolean validateCoordinates(double x, double y, double r) {
        return (Math.abs(x) <= r && Math.abs(y) <= r && (
                (x >= 0 && y <= 0 && (x * x + y * y <= r * r)) ||
                        (x <= 0 && x >= -r && y <= 0 && y >= -r / 2) ||
                        (x >= 0 && y >= 0 && (x + y <= r)))
        );
    }

    private void writeHitResponse(HitResponse resp) {
        String respStr = """
                HTTP/1.1 200 OK\r
                Content-Type: application/json\r
                Content-Length: %d\r
                \r
                %s""";
        String body = resp.json();
        System.out.printf(respStr, body.getBytes(StandardCharsets.UTF_8).length, body);
    }

    private void processDots() throws ParseException {
        DotsRequest req = parseDotsRequest();

        List<Dot> cached = new ArrayList<>();

        int from = (req.page() - 1) * req.size();
        int to = Math.min(req.page() * req.size(), cache.size());

        if (cache.isEmpty() || from >= cache.size()) {
            writeDotsResponse(new DotsResponse(req.page(), req.size(), false, false,
                    Collections.emptyList()));
        }

        for (HitRequest rq : cache.sequencedKeySet().stream()
                .toList().subList(from, to)) {
            HitResponse resp = cache.get(rq);
            cached.add(new Dot(rq, resp));
        }

        boolean hasBefore = req.page() != 1;
        boolean hasNext = to != cache.size();

        writeDotsResponse(new DotsResponse(req.page(), req.size(), hasBefore, hasNext, cached));
    }

    private DotsRequest parseDotsRequest() throws ParseException {
        Map<String, String> queries = QueryParser.parse(FCGIInterface.request.params.getProperty("QUERY_STRING"));
        if (queries.size() < 2) {
            throw new ParseException("Invalid query string: must provide at least 2 parameters.");
        }
        Integer page = null, size = null;
        for (String key : queries.keySet()) {
            try {
                if (key.equals("page")) page = Integer.valueOf(queries.get(key));
                if (key.equals("size")) size = Integer.valueOf(queries.get(key));
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid query string: must provide an integer.");
            }
        }
        if (page == null || size == null) {
            throw new ParseException("Invalid query string: must provide page and size.");
        }
        if (page < 0 || size < 0) {
            throw new ParseException("Invalid query string: page and size must be positive numbers.");
        }
        return new DotsRequest(page, size);
    }

    private void writeDotsResponse(DotsResponse resp) {
        String respStr = """
                HTTP/1.1 200 OK\r
                Content-Type: application/json\r
                Content-Length: %d\r
                \r
                %s""";
        String json = resp.json();
        System.out.printf(respStr, json.getBytes(StandardCharsets.UTF_8).length, json);
    }
}
