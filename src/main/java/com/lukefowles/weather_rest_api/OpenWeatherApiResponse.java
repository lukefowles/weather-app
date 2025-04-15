package com.lukefowles.weather_rest_api;

import java.util.List;
import java.util.Objects;

record OpenWeatherApiResponse(List<Weather> weather) {

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        OpenWeatherApiResponse response = (OpenWeatherApiResponse) object;
        return Objects.equals(weather, response.weather);
    }

}
