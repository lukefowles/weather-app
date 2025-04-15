package com.lukefowles.weather_rest_api;

class APIServerException extends RuntimeException {
    APIServerException(String message) {
        super(message);
    }
}
