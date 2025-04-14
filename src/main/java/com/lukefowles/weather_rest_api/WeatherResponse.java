package com.lukefowles.weather_rest_api;

public class WeatherResponse {
    private String description;

    public WeatherResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
