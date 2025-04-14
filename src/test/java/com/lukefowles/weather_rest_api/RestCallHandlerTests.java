package com.lukefowles.weather_rest_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RestCallHandler.class)
public class RestCallHandlerTests {
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private RestCallHandler restCallHandler;
    private final ObjectMapper mapper = new ObjectMapper();
    @Test
    void shouldReturnOpenWeatherResponse() throws JsonProcessingException {
        OpenWeatherApiResponse response = new OpenWeatherApiResponse(List.of(new Weather("rain")));
        server.expect(requestTo(containsString("openweathermap.org/data")))
                .andRespond(withSuccess(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
        assertThat(restCallHandler.callOpenWeatherApi("Melbourne, AU")).isEqualTo(response);
    }
    @Test
    void shouldThrowAPIServerExceptionWhenRestClientReturns5xx() throws JsonProcessingException {
        server.expect(requestTo(containsString("openweathermap.org/data")))
                .andRespond(MockRestResponseCreators.withBadGateway());
        assertThrows(APIServerException.class, () -> restCallHandler.callOpenWeatherApi("Melbourne, AU"));
    }

    @Test
    void shouldThrowAPIBadRequestExceptionWhenRestClientReturns4xx() throws JsonProcessingException {
        server.expect(requestTo(containsString("openweathermap.org/data")))
                .andRespond(MockRestResponseCreators.withBadRequest());
        assertThrows(BadRequestException.class, () -> restCallHandler.callOpenWeatherApi("Melbourne, AU"));
    }

    @Test
    void shouldThrowLocationNotFoundExceptionWhenRestClientReturns404() throws JsonProcessingException {
        server.expect(requestTo(containsString("openweathermap.org/data")))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatusCode.valueOf(404)));
        assertThrows(LocationNotFoundException.class, () -> restCallHandler.callOpenWeatherApi("Melbourne, AU"));
    }

    @Test
    void shouldThrowAPIServerExceptionWhenInternalRateLimitExceeded() throws JsonProcessingException {
        server.expect(requestTo(containsString("openweathermap.org/data")))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatusCode.valueOf(429)));
        assertThrows(APIServerException.class, () -> restCallHandler.callOpenWeatherApi("Melbourne, AU"));
    }

    @Test
    void shouldThrowAPIServerExceptionWhenInternalAPIKeyUnauthorised() throws JsonProcessingException {
        server.expect(requestTo(containsString("openweathermap.org/data")))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatusCode.valueOf(401)));
        assertThrows(APIServerException.class, () -> restCallHandler.callOpenWeatherApi("Melbourne, AU"));
    }
}
