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
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/api/target/api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
    "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "--add-opens", "java.base/java.io=ALL-UNNAMED", \
    "--add-opens", "java.base/java.util=ALL-UNNAMED", \
    "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", \
    "--add-opens", "java.base/java.nio=ALL-UNNAMED", \
    "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED", \
    "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED", \
    "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED", \
    "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED", \
    "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED", \
    "-jar", "app.jar"]