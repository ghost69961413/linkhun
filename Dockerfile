FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline
COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 linkhub
RUN mkdir -p /app/uploads && chown -R linkhub:linkhub /app
COPY --from=build /workspace/target/linkhub-0.0.1-SNAPSHOT.jar /app/linkhub.jar
USER 10001
ENV SPRING_PROFILES_ACTIVE=prod
# Keep Spring Data's startup-time repository parsing within Render's 512 MiB free tier.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=50.0 -XX:InitialRAMPercentage=8.0 -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/linkhub.jar"]
