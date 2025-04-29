package com.lukefowles.weather;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "requests")
class OpenWeatherApiCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String location;
    private Instant requestTime;
    private String description;

    public OpenWeatherApiCall(String location, Instant requestTime, String description) {
        this.location = location;
        this.requestTime = requestTime;
        this.description = description;
    }

    public OpenWeatherApiCall() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Instant requestTime) {
        this.requestTime = requestTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
