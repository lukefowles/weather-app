# Weather App

This is a Java 17/Spring Boot 3.4 project which uses the [OpenWeather API](https://openweathermap.org/api) to fetch the current weather at a location and return it
to the user. The endpoints for the project are:
- /weather : GET HttpMethod which returns a description of the weather at a given location.

## Design
The app is designed as a REST Api with access via the *WeatherController* class. From here, the *WeatherService* class
first queries the in-memory H2 database to see if an existing entry exists for the specified location. The db schema is as follows:

| calls |
| ____________ |
| BIGINT ID |
| VARCHAR(255) DESCRIPTION |
| VARCHAR(255) LOCATION |
| TIMESTAMPS WITH TIMEZONE |

The requestTime is stored within the db in order to check if existing entries are recent enough to be returned as a response to the client.
Since only a general description of the weather is being provided, it is assumed that any db entry within 5 minutes of the 
request time can be returned to the client, as to reduce the number of external OpenWeather API calls. If no such entry exists,
then the service will call the OpenWeather API, returning the response to the client and updating the DB with a record of the request.

## API rate limit and keys
5 api keys have been generated for the app to be passed as an x-api-key request header. The keys in this case are
arbitrary a listed below:
- firstKey
- secondKey
- thirdKey
- fourthKey
- fifthKey

A rate limiter utilising the external library *bucket4j* has been added to limit requests to 5 per hour
for each api key

## Running the app locally

The application is published as a docker image to the Github container registry. To use the app
on your local machine, first ensure you have a docker engine installed and running on your machine. Then run the following command in your terminal to
pull the image from the registry:
```shell
docker pull ghcr.io/lukefowles/weather-app:main
```
Note that you may be prompted to perform a docker login at this stage. 
Once you have pulled the image (please ensure you have a valid PAT token associated with your GitHub and added to your local machine), 
you can run the application and map to
local port 8080 using the following command:
```shell
docker run --publish  8080:8080  ghcr.io/lukefowles/weather-app:main
```

## Sample Requests

The /weather endpoint:
```shell
curl  -X GET \
  'http://localhost:8080/weather?city=Melbourne&country=AU' \
  --header 'Accept: application/json' \
  --header 'X-API-KEY: firstKey'
```
with sample response:
```json
{
  "description": "clear sky"
}
```