# --- build stage: Maven reactor (model + rules + service) -> fat jar ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -B -ntp clean package -DskipTests

# --- runtime stage: samo JRE + izvršni jar ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/service/target/service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
