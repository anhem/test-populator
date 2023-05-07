package com.github.anhem.testpopulator;

public class ObjectException extends RuntimeException {
    public ObjectException(String message) {
        super(message);
    }

    public ObjectException(String message, Exception e) {
        super(message, e);
    }
}
