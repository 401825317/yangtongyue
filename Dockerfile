FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/yangtongyue.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
