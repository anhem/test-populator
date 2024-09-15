package com.github.anhem.testpopulator.exception;

public class ObjectException extends RuntimeException {
    public ObjectException(String message) {
        super(message);
    }

    public ObjectException(String message, Exception e) {
        super(message, e);
    }
}
