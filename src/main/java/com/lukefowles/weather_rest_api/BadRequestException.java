package com.lukefowles.weather_rest_api;

public class BadRequestException extends RuntimeException {
    BadRequestException(String message) {
        super(message);
    }
}
