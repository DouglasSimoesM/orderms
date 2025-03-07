FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . /app/

RUN chmod +x ./mvnw

RUN ./mvnw -B clean dependency:resolve -DskipTests
RUN ./mvnw -B clean install -DskipTests

ENV APP_PROFILE=prod

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=${APP_PROFILE}", "-jar", "target/your-app.jar"]
