package com.lukefowles.exception;

public class APIServerException extends RuntimeException {
    public APIServerException(String message) {
        super(message);
    }
}
