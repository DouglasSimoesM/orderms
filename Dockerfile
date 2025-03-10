FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . /app/

RUN chmod +x ./mvnw

RUN ./mvnw -B clean dependency:resolve -DskipTests
RUN ./mvnw -B clean install -DskipTests

ENV APP_PROFILE=prod

EXPOSE 8080
EXPOSE 5672
EXPOSE 15672

CMD ["java", "-Dspring.profiles.active=${APP_PROFILE}", "-jar", "target/orderms-0.0.1-SNAPSHOT.jar"]
