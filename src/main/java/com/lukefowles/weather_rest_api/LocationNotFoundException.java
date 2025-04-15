package com.lukefowles.weather_rest_api;

class LocationNotFoundException extends RuntimeException {
    LocationNotFoundException(String message) {
        super(message);
    }
}
