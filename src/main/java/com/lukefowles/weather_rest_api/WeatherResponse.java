package com.lukefowles.weather_rest_api;

import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        WeatherResponse response = (WeatherResponse) object;
        return Objects.equals(description, response.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(description);
    }
}
