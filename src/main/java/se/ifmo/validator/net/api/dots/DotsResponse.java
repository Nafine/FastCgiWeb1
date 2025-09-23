package se.ifmo.validator.net.api.dots;

import java.util.List;
import java.util.stream.Collectors;

public record DotsResponse(int page, int size, boolean hasBefore, boolean hasNext, List<Dot> dots) {
    public String json() {
        String json = """
                {"page": %d, "size": %d, "hasBefore": %b, "hasNext": %b, "dots": %s}
                """;
        String dotsJsonArray = "[%s]".formatted(dots.stream().map(Dot::json).collect(Collectors.joining(",")));
        return json.formatted(page, size, hasBefore, hasNext, dotsJsonArray);
    }
}
