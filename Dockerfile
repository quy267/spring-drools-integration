# Multi-stage Dockerfile for Spring Boot Drools Integration
# Stage 1: Build stage
FROM openjdk:17-jdk-slim AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better layer caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime stage
FROM openjdk:17-jre-slim AS runtime

# Install curl for health checks and create non-root user with security hardening
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* && \
    groupadd -r appuser --gid=1001 && \
    useradd -r -g appuser --uid=1001 -d /app -s /sbin/nologin appuser && \
    # Remove unnecessary packages and files for security
    apt-get purge -y --auto-remove && \
    # Set secure permissions
    chmod 755 /app

# Set working directory
WORKDIR /app

# Create necessary directories and set permissions
RUN mkdir -p /app/logs /app/rules /app/uploads /app/backups && \
    chown -R appuser:appuser /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy rules and configuration files
COPY --from=builder /app/src/main/resources/rules ./rules/
COPY --from=builder /app/src/main/resources/application*.properties ./

# Change ownership of all files to appuser
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Health check command
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]