package com.lukefowles.weather_rest_api;

import java.util.Objects;

class Weather {
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

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Weather weather = (Weather) object;
        return Objects.equals(description, weather.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(description);
    }
}
