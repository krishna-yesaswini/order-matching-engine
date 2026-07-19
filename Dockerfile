# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY api/pom.xml api/pom.xml
COPY benchmark/pom.xml benchmark/pom.xml
COPY core/src core/src
COPY api/src api/src
COPY benchmark/src benchmark/src
RUN mvn clean package -pl api -am -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/api/target/api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]