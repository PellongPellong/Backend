## Build stage
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Gradle wrapper and build scripts first (better layer caching).
COPY gradlew build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle

# Download dependencies (will be cached if build scripts don't change).
RUN ./gradlew --no-daemon dependencies

# Copy source and build a Spring Boot fat JAR (tests are not run here).
COPY src /app/src
RUN ./gradlew --no-daemon bootJar \
  && JAR_FILE="$(ls -1 build/libs/*.jar | grep -v -- '-plain.jar' | head -n 1)" \
  && cp "$JAR_FILE" /app/app.jar

## Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh","-lc","java $JAVA_OPTS -jar /app/app.jar"]
