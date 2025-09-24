package se.ifmo.validator.net.api.dots;

import se.ifmo.validator.net.api.hit.HitRequest;
import se.ifmo.validator.net.api.hit.HitResponse;

public record Dot(HitRequest req, HitResponse res) {
    public String json() {
        return """
                {"req": %s, "resp": %s}""".formatted(req.json(), res.json());
    }
}
