package se.ifmo.validator.net.api.hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record HitResponse(Boolean hit, LocalDateTime timestamp, long execTime) {
    public String json() {
        return """
                {"hit": %b, "requestTime": "%s", "execTime": %d}"""
                .formatted(hit, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), execTime);
    }
}
