package com.lukefowles.weather_rest_api;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ApiCalls")
public class OpenWeatherApiCall {

    @GeneratedValue
    private long id;
    private Instant requestTime;
    private OpenWeatherApiResponse openWeatherApiResponse;

    public OpenWeatherApiCall(OpenWeatherApiResponse openWeatherApiResponse, Instant requestTime) {
        this.openWeatherApiResponse = openWeatherApiResponse;
        this.requestTime = requestTime;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Instant requestTime) {
        this.requestTime = requestTime;
    }

    public OpenWeatherApiResponse getOpenWeatherApiResponse() {
        return openWeatherApiResponse;
    }

    public void setOpenWeatherApiResponse(OpenWeatherApiResponse openWeatherApiResponse) {
        this.openWeatherApiResponse = openWeatherApiResponse;
    }
}
