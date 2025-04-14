package com.lukefowles.weather_rest_api;

public class Weather {
    private String description;
    public Weather(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
