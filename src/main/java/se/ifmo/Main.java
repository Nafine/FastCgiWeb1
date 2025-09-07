package se.ifmo;

import se.ifmo.validator.net.ValidatorService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Version 1.0");
        (new ValidatorService()).run();
    }
}
