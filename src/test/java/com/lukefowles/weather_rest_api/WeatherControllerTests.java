package com.lukefowles.weather_rest_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class WeatherControllerTests {

    private MockMvc mvc;

    @Mock
    private WeatherService weatherService;

    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    void getWeatherReturnsWeatherResponseAnd200() throws Exception {
        WeatherResponse weatherResponse = new WeatherResponse("Rain");
        when(weatherService.getWeatherResponse(any(), any())).thenReturn(weatherResponse);
        MockHttpServletResponse response = mvc.perform(
                        get("/weather").
                                contentType(MediaType.APPLICATION_JSON)
                                .param("city", "Melbourne")
                                .param("country", "AU")).
                andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mapper.writeValueAsString(weatherResponse));
    }
}
