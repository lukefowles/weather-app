package com.lukefowles.weather_rest_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam(value = "city") String city,
                                                      @RequestParam(value = "country") String country) {
//        return new ResponseEntity<>(weatherService.getWeatherResponse(city, country), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
