package se.ifmo.http;

import lombok.Getter;

import java.io.InputStream;
import java.util.Map;

@Getter
public class HTTPRequest {
    String method;
    String uri;
    String proto;
    Map<String, String> header;
    InputStream bodyReader;
    int contentLength;

    public static class Builder {
        private HTTPRequest request = new HTTPRequest();

        public HTTPRequest build() {
            if (request.method == null ||
                    request.uri == null ||
                    request.proto == null ||
                    request.bodyReader == null
            ) {
                throw new IllegalStateException("""
                        You must provide at least:
                        http method,
                        request uri,
                        proto,
                        body reader""");
            }
            return request;
        }

        public void clear() {
            request = new HTTPRequest();
        }

        public Builder method(String method) {
            if (method == null || !method.equals("GET")
                    && !method.equals("POST")
                    && !method.equals("PUT")
                    && !method.equals("DELETE")) {
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
            }
            request.method = method;
            return this;
        }

        public Builder uri(String uri) {
            request.uri = uri;
            return this;
        }

        public Builder proto(String proto) {
            request.proto = proto;
            return this;
        }

        public Builder header(Map<String, String> header) {
            if (header == null) {
                throw new NullPointerException("Header cannot be null");
            }
            request.header = header;
            return this;
        }

        public Builder bodyReader(InputStream bodyReader) {
            request.bodyReader = bodyReader;
            return this;
        }

        public Builder contentLength(int contentLength) {
            if (contentLength < 0) {
                throw new IllegalArgumentException("Invalid content length: " + contentLength);
            }
            request.contentLength = contentLength;
            return this;
        }
    }
}
