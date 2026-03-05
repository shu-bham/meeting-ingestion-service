package com.soulside.exception;

public class KafkaConnectionException extends RuntimeException {
    public KafkaConnectionException(String message) {
        super(message);
    }
}
