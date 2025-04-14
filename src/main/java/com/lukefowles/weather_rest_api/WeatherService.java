package com.lukefowles.weather_rest_api;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private WeatherApiCallRepository apiCallRepo;
    public WeatherService(WeatherApiCallRepository apiCallRepo) {
        this.apiCallRepo = apiCallRepo;
    }

    WeatherResponse getWeatherResponse(String city, String country) {
        String location = getLocationFromRequest(city, country);
        System.out.println(location);
        OpenWeatherApiCall entry = apiCallRepo.getApiCallByLocation(location)
                .orElseGet(() -> null);
        return new WeatherResponse(entry.getDescription());
    }



    private String getLocationFromRequest(String city, String country) {
        return city + ", " + country;
    }
}
