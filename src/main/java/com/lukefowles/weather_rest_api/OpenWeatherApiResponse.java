package com.lukefowles.weather_rest_api;

import java.util.List;
import java.util.Objects;

public class OpenWeatherApiResponse {
    private List<Weather> weather;
    public OpenWeatherApiResponse(List<Weather> weather) {
        this.weather = weather;
    }
    public List<Weather> getWeather() {
        return weather;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        OpenWeatherApiResponse response = (OpenWeatherApiResponse) object;
        return Objects.equals(weather, response.weather);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(weather);
    }
}
