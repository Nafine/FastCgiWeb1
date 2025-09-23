package se.ifmo.fcgi.handler;

import se.ifmo.fcgi.api.Context;
import se.ifmo.validator.net.parse.ParseException;


public interface Handler {
    void process() throws Exception;
}
