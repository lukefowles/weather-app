package com.lukefowles;

import com.lukefowles.security.RateLimiter;
import com.lukefowles.weather.WeatherApiCallRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class WeatherRestApiApplicationTests {

	@Autowired
	WeatherApiCallRepository weatherApiCallRepository;

	@Autowired
	RateLimiter rateLimiter;

	@LocalServerPort
	private int port;

	static MockServerClient mockServerClient;

	@Container
	static MockServerContainer mockServerContainer = new MockServerContainer(
			DockerImageName.parse("mockserver/mockserver:5.15.0")
	);

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		mockServerClient =
				new MockServerClient(
						mockServerContainer.getHost(),
						mockServerContainer.getServerPort()
				);
		registry.add("openWeather.baseURI", mockServerContainer::getEndpoint);

	}

	@BeforeEach
	public void beforeEach() {
		RestAssured.port = port;
		mockServerClient.reset();
		weatherApiCallRepository.deleteAll();
		rateLimiter.clearBucket("firstKey");
	}

	@Test
	void getWeatherReturnsCorrectResponseAnd200() throws IOException {
		String weatherAPIResponse = FileUtils.readFileToString(new File("src/test/resources/OpenWeatherApiResponse.json"), StandardCharsets.UTF_8);

		mockServerClient
				.when(
						request().withMethod("GET")
				)
				.respond(
						response()
								.withStatusCode(200)
								.withContentType(MediaType.APPLICATION_JSON)
								.withBody(json(weatherAPIResponse)));

		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(200)
				.body("description", is("moderate rain"));
	}


	@Test
	void getWeatherReturnsResponseFromDbAnd200ForDuplicateRequests() throws IOException {
		String weatherAPIResponse = FileUtils.readFileToString(new File("src/test/resources/OpenWeatherApiResponse.json"), StandardCharsets.UTF_8);

		mockServerClient
				.when(request().withMethod("GET"))
				.respond(
						response()
								.withStatusCode(200)
								.withContentType(MediaType.APPLICATION_JSON)
								.withBody(json(weatherAPIResponse)));

		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(200)
				.body("description", is("moderate rain"));
		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(200)
				.body("description", is("moderate rain"));
		mockServerClient.verify(request().withMethod("GET"),
				VerificationTimes.exactly(1));
	}

	@Test
	void getWeatherReturns401WhenIncorrectApiKey() {
		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "incorrect key")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(401);
	}

	@Test
	void getWeatherReturns404StatusCodeWhenLocationNotFound() {
		mockServerClient
				.when(
						request().withMethod("GET"), Times.exactly(1)
				)
				.respond(
						response()
								.withStatusCode(404));

		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(404);
	}

	@Test
	void getWeatherReturns5xxErrorWhenOpenWeatherReturns5xxError() {
		mockServerClient
				.when(
						request().withMethod("GET"), Times.exactly(1)
				)
				.respond(
						response()
								.withStatusCode(500));

		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(502);
	}

	@Test
	void getWeatherReturns400ErrorWhenMissingParam() {
		mockServerClient
				.when(
						request().withMethod("GET"), Times.exactly(1)
				)
				.respond(
						response()
								.withStatusCode(500));

		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.get("/weather")
				.then()
				.statusCode(400);
	}

	@Test
	void getWeatherReturns429WhenRateLimitExceeded() throws IOException {
		String weatherAPIResponse = FileUtils.readFileToString(new File("src/test/resources/OpenWeatherApiResponse.json"), StandardCharsets.UTF_8);
		mockServerClient
			.when(
					request().withMethod("GET")
			)
			.respond(
					response()
							.withStatusCode(200)
							.withContentType(MediaType.APPLICATION_JSON)
							.withBody(json(weatherAPIResponse)));
		for(int i=0; i<2; i++) {
			given()
					.contentType(ContentType.JSON)
					.when()
					.header("X-API-KEY", "firstKey")
					.param("city", "Turin")
					.param("country", "IT")
					.get("/weather")
					.then()
					.statusCode(200)
					.body("description", is("moderate rain"));
		}
		given()
				.contentType(ContentType.JSON)
				.when()
				.header("X-API-KEY", "firstKey")
				.param("city", "Turin")
				.param("country", "IT")
				.get("/weather")
				.then()
				.statusCode(429);
	}
}
