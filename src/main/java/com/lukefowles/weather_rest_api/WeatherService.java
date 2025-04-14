package com.lukefowles.weather_rest_api;

import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

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
                .orElseGet(() -> callOpenWeatherAPI(location));
        return new WeatherResponse(entry.getDescription());
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
        System.out.println("hit");
        apiCallRepo.save(apiCallRecord);
        return apiCallRecord;
    }


    private String getLocationFromRequest(String city, String country) {
        return city + ", " + country;
    }
}
