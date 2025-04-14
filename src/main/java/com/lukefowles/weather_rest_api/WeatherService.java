package com.lukefowles.weather_rest_api;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
public class WeatherService {
    private WeatherApiCallRepository apiCallRepo;
    private RestCallHandler restCallHandler;

    public WeatherService(WeatherApiCallRepository apiCallRepo, RestCallHandler restCallHandler) {
        this.apiCallRepo = apiCallRepo;
        this.restCallHandler = restCallHandler;
    }

    WeatherResponse getWeatherResponse(String city, String country) {
        String location = getLocationFromRequest(city, country);
        OpenWeatherApiCall entry = apiCallRepo.getApiCallByLocation(location)
                .filter(this::isEntryStale)
                .orElseGet(() ->callOpenWeatherAPI(location));
        return new WeatherResponse(entry.getDescription());
    }

    private boolean isEntryStale(OpenWeatherApiCall entry) {
        return entry.getRequestTime().isAfter(Instant.now().minusSeconds(300));
    }

    private OpenWeatherApiCall callOpenWeatherAPI(String location) {

        OpenWeatherApiResponse response = restCallHandler.callOpenWeatherApi(location);
//                restClient
//                .get()
//                .uri(uriBuilder -> {
//                    return  uriBuilder
//                            .queryParam("q", location)
//                            .build();
//                })
//                .retrieve()
////                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED),
////                        (req, resp) -> {throw new UnauthorisedException(UNAUTHORISED_EXCEPTION);})
////                .onStatus(HttpStatusCode::is5xxServerError,
////                        (req, resp) -> {throw new APIServerException(API_SERVER_EXCEPTION);})
////                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.valueOf(429)),
////                        (req, resp) -> {throw new RateLimitExceededException(RATE_LIMIT_EXCEEDED_EXCEPTION);})
////                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.valueOf(400)),
////                        (req, resp) -> {throw new BadRequestException(BAD_REQUEST_EXCEPTION);})
////                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.valueOf(404)),
////                        (req, resp) -> {throw new LocationNotFoundException(LOCATION_NOT_FOUND);})
//                .body(OpenWeatherApiResponse.class);
        OpenWeatherApiCall apiCallRecord = new OpenWeatherApiCall(location, Instant.now(), response.getWeather().get(0).getDescription());
        updateOrInsertToRepo(apiCallRecord);
        return apiCallRecord;
    }

    private void updateOrInsertToRepo(OpenWeatherApiCall apiCallRecord) {
        apiCallRepo.getApiCallByLocation(apiCallRecord.getLocation())
                .ifPresentOrElse(entry -> {
                    entry.setDescription(apiCallRecord.getDescription());
                    entry.setRequestTime(apiCallRecord.getRequestTime());
                    apiCallRepo.save(entry);},
                            () -> apiCallRepo.save(apiCallRecord));
    }


    private String getLocationFromRequest(String city, String country) {
        return city + ", " + country;
    }
}
