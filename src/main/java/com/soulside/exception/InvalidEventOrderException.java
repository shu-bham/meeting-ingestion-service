package com.soulside.exception;

public class InvalidEventOrderException extends RuntimeException {
    public InvalidEventOrderException(String message) {
        super(message);
    }
}
