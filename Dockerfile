# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
# First copy the pom.xml and download dependencies to leverage Docker cache
COPY pom.xml .
# Copy the source code
COPY src ./src
# Build the jar, skipping tests to speed up the docker build
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the built jar from the builder stage
COPY --from=builder /app/target/studyChat-0.0.1-SNAPSHOT.jar app.jar
# Create a volume for the crawler output so it can be persisted on the host
VOLUME /app/collected-content

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
