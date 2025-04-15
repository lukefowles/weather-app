package com.lukefowles.weather_rest_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class WeatherServiceTests {

    @Mock
    WeatherApiCallRepository apiCallRepo;

    @Mock
    RestCallHandler restCallHandler;

    @InjectMocks
    WeatherService weatherService;

    @Captor
    ArgumentCaptor<OpenWeatherApiCall> apiCallCaptor;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnWeatherResponseFromDb() {
        OpenWeatherApiCall apiCall = new OpenWeatherApiCall("Melbourne, AU", Instant.now(), "Sunny");
        WeatherResponse expectedResponse = new WeatherResponse("Sunny");
        when(apiCallRepo.findByLocation("Melbourne, AU")).thenReturn(List.of(apiCall));
        assertThat(weatherService.getWeatherResponse("Melbourne", "AU"))
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnWeatherResponseWhenNotPresentInDb() {
        OpenWeatherApiCall expectedApiCallSave = new OpenWeatherApiCall("Turin, IT", Instant.now(), "moderate rain");
        WeatherResponse expectedResponse = new WeatherResponse("moderate rain");
        OpenWeatherApiResponse mockedApiResponse = new OpenWeatherApiResponse(List.of(new Weather("moderate rain")));
        when(apiCallRepo.findByLocation("Turin, IT")).thenReturn(List.of());
        when(restCallHandler.callOpenWeatherApi("Turin, IT")).thenReturn(mockedApiResponse);
        assertThat(weatherService.getWeatherResponse("Turin", "IT"))
                .isEqualTo(expectedResponse);
        verify(apiCallRepo).save(apiCallCaptor.capture());
        assertThat(apiCallCaptor.getValue().getDescription()).isEqualTo(expectedApiCallSave.getDescription());
        assertThat(apiCallCaptor.getValue().getLocation()).isEqualTo(expectedApiCallSave.getLocation());
    }

    @Test
    void shouldReturnWeatherResponseFromOpenWeatherWhenDbEntryIsStale() {
        OpenWeatherApiCall staleApiCall = new OpenWeatherApiCall("Turin, IT", Instant.now().minusSeconds(301), "sunny");
        OpenWeatherApiCall expectedApiCallSave = new OpenWeatherApiCall("Turin, IT", Instant.now(), "moderate rain");
        WeatherResponse expectedResponse = new WeatherResponse("moderate rain");
        OpenWeatherApiResponse mockedApiResponse = new OpenWeatherApiResponse(List.of(new Weather("moderate rain")));
        when(apiCallRepo.findByLocation("Turin, IT")).thenReturn(List.of(staleApiCall));
        when(restCallHandler.callOpenWeatherApi("Turin, IT")).thenReturn(mockedApiResponse);
        assertThat(weatherService.getWeatherResponse("Turin", "IT"))
                .isEqualTo(expectedResponse);
        verify(apiCallRepo).save(apiCallCaptor.capture());
        assertThat(apiCallCaptor.getValue().getDescription()).isEqualTo(expectedApiCallSave.getDescription());
        assertThat(apiCallCaptor.getValue().getLocation()).isEqualTo(expectedApiCallSave.getLocation());
    }
}
