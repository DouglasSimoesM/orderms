FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . /app/

RUN chmod +x ./mvnw

RUN ./mvnw -B clean dependency:list -DskipTests

RUN ./mvnw -B clean install -DskipTests

ENV APP_PROFILE=prod

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]
