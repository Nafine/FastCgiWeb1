package se.ifmo.validator.net.api.dots;

import se.ifmo.validator.net.api.hit.HitRequest;

public record Dot(HitRequest req, Boolean hit) {
    public String json() {
        return """
                {"dot": {"x": %f, "y": %f, "r": %f}, "hit": %b}"""
                .formatted(req.x(), req.y(), req.r(), hit);
    }
}
