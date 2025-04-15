package com.lukefowles.weather_rest_api;

class BadRequestException extends RuntimeException {
    BadRequestException(String message) {
        super(message);
    }
}
