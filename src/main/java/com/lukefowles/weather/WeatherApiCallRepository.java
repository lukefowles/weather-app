package com.lukefowles.weather;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherApiCallRepository extends JpaRepository<OpenWeatherApiCall, Long> {
    List<OpenWeatherApiCall> findByLocation(String location);
}
