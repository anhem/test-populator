package com.github.anhem.testpopulator;

public class PopulateException extends RuntimeException {

    public PopulateException(String message) {
        super(message);
    }

    public PopulateException(String message, Exception e) {
        super(message, e);
    }
}
