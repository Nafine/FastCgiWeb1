package se.ifmo.validator.net.parse;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {
    public static Map<String, String> parse(String querySegment) throws ParseException {
        if (querySegment == null || querySegment.isEmpty()) {
            throw new ParseException("Cannot parse empty query segment.");
        }
        Map<String, String> map = new HashMap<>();
        String[] query = querySegment.split("&");

        for (String fragment : query) {
            String[] parts = fragment.split("=");
            if (parts.length != 2) {
                throw new ParseException("Illegal query fragment: " + fragment);
            }
            String key = parts[0];
            String value = parts[1];
            map.put(key, value);
        }
        return map;
    }
}
