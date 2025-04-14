package com.lukefowles.weather_rest_api;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class WeatherServiceTests {

    @Mock
    WeatherApiCallRepository apiCallRepo;

    @InjectMocks
    WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnWeatherResponseFromDb() {
        OpenWeatherApiCall apiCall = new OpenWeatherApiCall("Melbourne, AU", Instant.now(), "Sunny");
        WeatherResponse expectedResponse = new WeatherResponse("Sunny");
        when(apiCallRepo.getApiCallByLocation("Melbourne, AU")).thenReturn(Optional.of(apiCall));
        assertThat(weatherService.getWeatherResponse("Melbourne", "AU"))
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnWeatherResponseWhenNotPresentInDb() throws IOException {
        String openWeatherApiResponse = FileUtils.readFileToString(new File("src/test/resources/OpenWeatherAPIResponse.json"), StandardCharsets.UTF_8);
        OpenWeatherApiCall apiCall = new OpenWeatherApiCall("Turin, IT", Instant.now(), "moderate rain");
        WeatherResponse expectedResponse = new WeatherResponse("moderate rain");
        when(apiCallRepo.getApiCallByLocation("Turin, IT")).thenReturn(Optional.empty());
        assertThat(weatherService.getWeatherResponse("Turin", "IT"))
                .isEqualTo(expectedResponse);
    }
}
