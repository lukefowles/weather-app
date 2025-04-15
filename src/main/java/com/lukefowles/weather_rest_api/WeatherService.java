package com.lukefowles.weather_rest_api;

import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
class WeatherService {
    private final WeatherApiCallRepository apiCallRepo;
    private final RestCallHandler restCallHandler;

    public WeatherService(WeatherApiCallRepository apiCallRepo, RestCallHandler restCallHandler) {
        this.apiCallRepo = apiCallRepo;
        this.restCallHandler = restCallHandler;
    }

    WeatherResponse getWeatherResponse(String city, String country) {
        String location = getLocationFromRequest(city, country);
        OpenWeatherApiCall entry = apiCallRepo.findByLocation(location)
                .stream()
                .findFirst()
                .filter(this::isEntryStale)
                .orElseGet(() -> callOpenWeatherAPI(location));
        updateOrInsertToRepo(entry);
        return new WeatherResponse(entry.getDescription());
    }

    private boolean isEntryStale(OpenWeatherApiCall entry) {
        return entry.getRequestTime().isAfter(Instant.now().minusSeconds(300));
    }

    private OpenWeatherApiCall callOpenWeatherAPI(String location) {
        OpenWeatherApiResponse response = restCallHandler.callOpenWeatherApi(location);
        return new OpenWeatherApiCall(location, Instant.now(), response.getWeather().get(0).getDescription());
    }

    private void updateOrInsertToRepo(OpenWeatherApiCall apiCallRecord) {
        apiCallRepo.findByLocation(apiCallRecord.getLocation())
                .stream()
                .findFirst()
                .ifPresentOrElse(entry -> {
                    entry.setDescription(apiCallRecord.getDescription());
                    entry.setRequestTime(apiCallRecord.getRequestTime());
                    apiCallRepo.save(entry);},
                            () -> {
                    apiCallRepo.save(apiCallRecord);});
    }

    private String getLocationFromRequest(String city, String country) {
        return city + ", " + country;
    }
}
