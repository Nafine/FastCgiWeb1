package se.ifmo.fcgi.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import se.ifmo.http.HTTPRequest;

import java.io.PrintStream;

@Getter
@AllArgsConstructor
public class Context {
    HTTPRequest request;
    PrintStream writer;

    public void write(String str){
        writer.print(str);
    }
}
