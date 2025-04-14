package com.lukefowles.weather_rest_api;

public class APIServerException extends RuntimeException {
    APIServerException(String message) {
        super(message);
    }
}
