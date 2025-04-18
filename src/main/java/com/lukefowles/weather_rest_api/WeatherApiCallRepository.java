package com.lukefowles.weather_rest_api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface WeatherApiCallRepository extends JpaRepository<OpenWeatherApiCall, Long> {
    List<OpenWeatherApiCall> findByLocation(String location);
}
