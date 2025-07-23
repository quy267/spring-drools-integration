# Multi-stage Dockerfile for Spring Boot Drools Integration
# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

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
FROM eclipse-temurin:17-jre-alpine AS runtime

# Create non-root user with security hardening
RUN apk add --no-cache ca-certificates && \
    mkdir -p /app && \
    addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser -h /app -s /sbin/nologin -D && \
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

# Health check command (commented out - requires wget)
# HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
#     CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]