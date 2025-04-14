package com.lukefowles.weather_rest_api;

public class LocationNotFoundException extends RuntimeException {
    LocationNotFoundException(String message) {
        super(message);
    }
}
