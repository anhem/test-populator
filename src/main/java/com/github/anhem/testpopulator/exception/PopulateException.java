package com.github.anhem.testpopulator.exception;

public class PopulateException extends RuntimeException {

    public PopulateException(String message) {
        super(message);
    }

    public PopulateException(String message, Exception e) {
        super(message, e);
    }

    public PopulateException(Throwable cause) {
        super(cause);
    }
}
