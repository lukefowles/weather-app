package com.lukefowles.weather_rest_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.lukefowles.weather_rest_api.Constants.*;

@Component
public class RestCallHandler {

    private final RestClient restClient;

    private final String apiKey;

    RestCallHandler(RestClient.Builder restClient,
                   @Value("${openWeather.baseURI}") String baseURI,
                   @Value("${openWeather.apiKey}") String apiKey) {
        this.restClient = restClient
                .baseUrl(baseURI)
                .build();
        this.apiKey = apiKey;
    }
    public OpenWeatherApiResponse callOpenWeatherApi(String location) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder
                            .queryParam("q", location)
                            .queryParam("appId", apiKey)
                            .build();
                })
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (req, resp) -> {throw new APIServerException(API_SERVER_EXCEPTION);})
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, resp) -> {throw new APIServerException(API_SERVER_EXCEPTION);})
                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.valueOf(429)),
                        (req, resp) -> {throw new APIServerException(API_SERVER_EXCEPTION);})
                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.valueOf(400)),
                        (req, resp) -> {throw new BadRequestException(BAD_REQUEST_EXCEPTION);})
                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(HttpStatus.valueOf(404)),
                        (req, resp) -> {throw new LocationNotFoundException(LOCATION_NOT_FOUND);})
                .body(OpenWeatherApiResponse.class);
    }
}
