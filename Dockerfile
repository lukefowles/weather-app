FROM eclipse-temurin:17
EXPOSE 8080
COPY  /build/libs/weather-rest-api-0.0.1-SNAPSHOT.jar weatherApp.jar
ENTRYPOINT ["java", "-jar", "weatherApp.jar"]