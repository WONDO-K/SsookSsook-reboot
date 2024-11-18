# 1. Base image
FROM openjdk:17-jdk-alpine AS builder

# 2. Set working directory
WORKDIR /app

# 3. Copy the JAR file into the container
COPY ./build/libs/Ssook_BE-0.0.1-SNAPSHOT.jar app.jar

# 4. Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
