# Container Deployment Guide

This guide provides comprehensive instructions for containerizing and deploying the Spring Boot Drools Integration application using Docker and Docker Compose.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Docker Configuration](#docker-configuration)
- [Docker Compose Setup](#docker-compose-setup)
- [Deployment Scripts](#deployment-scripts)
- [Environment Configuration](#environment-configuration)
- [Health Checks and Monitoring](#health-checks-and-monitoring)
- [Security Considerations](#security-considerations)
- [Troubleshooting](#troubleshooting)
- [Production Deployment](#production-deployment)

## Prerequisites

Before deploying the application in containers, ensure you have the following installed:

- **Docker**: Version 20.10 or later
- **Docker Compose**: Version 2.0 or later
- **Git**: For cloning the repository
- **curl**: For health checks (optional)

### Verify Installation

```bash
docker --version
docker-compose --version
# or
docker compose version
```

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-drools-integration
```

### 2. Build and Deploy

```bash
# Build the Docker image
./scripts/build.sh

# Deploy with Docker Compose
./scripts/deploy.sh development up
```

### 3. Verify Deployment

```bash
# Check service status
./scripts/deploy.sh development status

# Check application health
curl http://localhost:8080/actuator/health
```

### 4. Access the Application

- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin123)

## Docker Configuration

### Dockerfile Overview

The application uses a multi-stage Dockerfile with the following features:

#### Stage 1: Build Stage
- **Base Image**: `openjdk:17-jdk-slim`
- **Purpose**: Compile the application using Maven
- **Optimizations**: 
  - Layer caching for dependencies
  - Maven wrapper execution
  - Clean package build

#### Stage 2: Runtime Stage
- **Base Image**: `openjdk:17-jre-slim`
- **Security Features**:
  - Non-root user (`appuser` with UID/GID 1001)
  - Minimal system packages
  - Secure file permissions
- **Health Check**: Built-in health check using curl
- **JVM Optimizations**: Container-aware JVM settings

### Key Features

```dockerfile
# Multi-stage build for smaller image size
FROM openjdk:17-jdk-slim AS builder
# ... build stage

FROM openjdk:17-jre-slim AS runtime
# Security hardening
RUN groupadd -r appuser --gid=1001 && \
    useradd -r -g appuser --uid=1001 -d /app -s /sbin/nologin appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
```

### Build Arguments and Labels

The Dockerfile supports build arguments for metadata:

```bash
docker build \
  --build-arg BUILD_DATE="$(date -u +'%Y-%m-%dT%H:%M:%SZ')" \
  --build-arg GIT_COMMIT="$(git rev-parse --short HEAD)" \
  --build-arg VERSION="1.0.0" \
  --tag spring-drools-integration:1.0.0 .
```

## Docker Compose Setup

### Services Overview

The Docker Compose configuration includes:

1. **spring-drools-app**: Main application container
2. **postgres**: PostgreSQL database
3. **prometheus**: Metrics collection
4. **grafana**: Metrics visualization
5. **redis**: Caching layer (optional)

### Service Configuration

#### Application Service

```yaml
spring-drools-app:
  build:
    context: .
    dockerfile: Dockerfile
  ports:
    - "8080:8080"
  environment:
    - SPRING_PROFILES_ACTIVE=docker
    - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ruledb
  volumes:
    - ./rules:/app/rules:ro
    - app-logs:/app/logs
  depends_on:
    postgres:
      condition: service_healthy
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 60s
```

#### Database Service

```yaml
postgres:
  image: postgres:15-alpine
  environment:
    - POSTGRES_DB=ruledb
    - POSTGRES_USER=ruleuser
    - POSTGRES_PASSWORD=rulepass
  volumes:
    - postgres-data:/var/lib/postgresql/data
    - ./docker/postgres/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ruleuser -d ruledb"]
    interval: 10s
    timeout: 5s
    retries: 5
```

### Networking and Volumes

```yaml
networks:
  spring-drools-network:
    driver: bridge

volumes:
  postgres-data:
  prometheus-data:
  grafana-data:
  app-logs:
  app-uploads:
  app-backups:
```

## Deployment Scripts

### Build Script (`scripts/build.sh`)

Builds the Docker image with proper tagging and optimization:

```bash
# Build with default settings
./scripts/build.sh

# Build with specific version
./scripts/build.sh 1.0.0

# Build and push to registry
./scripts/build.sh 1.0.0 --push
```

**Features**:
- Automatic version tagging
- Git commit labeling
- Build date metadata
- Image cleanup
- Registry support

### Deployment Script (`scripts/deploy.sh`)

Manages Docker Compose deployments across environments:

```bash
# Deploy to development
./scripts/deploy.sh development up

# Deploy to production
./scripts/deploy.sh production up

# Show logs
./scripts/deploy.sh development logs

# Check status
./scripts/deploy.sh development status

# Cleanup
./scripts/deploy.sh development cleanup
```

**Supported Environments**:
- `development` / `dev`
- `staging` / `stage`
- `production` / `prod`

**Available Actions**:
- `up` / `start`: Start services
- `down` / `stop`: Stop services
- `restart`: Restart services
- `logs`: Show logs
- `status` / `ps`: Show status
- `cleanup`: Remove all resources

### Management Script (`scripts/manage.sh`)

Provides container management utilities:

```bash
# Show logs
./scripts/manage.sh logs

# Open shell
./scripts/manage.sh shell

# Execute command
./scripts/manage.sh exec "ps aux"

# Health check
./scripts/manage.sh health

# Backup data
./scripts/manage.sh backup

# Show statistics
./scripts/manage.sh stats
```

## Environment Configuration

### Development Environment

```bash
export SPRING_PROFILES_ACTIVE="dev,docker"
export LOG_LEVEL="DEBUG"
export POSTGRES_DB="ruledb_dev"
```

### Staging Environment

```bash
export SPRING_PROFILES_ACTIVE="staging,docker"
export LOG_LEVEL="INFO"
export POSTGRES_DB="ruledb_staging"
```

### Production Environment

```bash
export SPRING_PROFILES_ACTIVE="prod,docker"
export LOG_LEVEL="WARN"
export POSTGRES_DB="ruledb_prod"
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profiles | `docker` |
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:postgresql://postgres:5432/ruledb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `ruleuser` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `rulepass` |
| `APP_RULES_UPLOAD_DIR` | Rules upload directory | `/app/uploads` |
| `APP_RULES_BACKUP_DIR` | Rules backup directory | `/app/backups` |
| `JAVA_OPTS` | JVM options | Container optimized |

## Health Checks and Monitoring

### Application Health Checks

The application provides comprehensive health checks:

#### Built-in Health Indicators

1. **Drools Health Indicator**: Checks rule engine status
2. **Decision Table Health Indicator**: Validates decision tables
3. **Database Health Indicator**: Verifies database connectivity

#### Health Check Endpoints

```bash
# Overall health
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health | jq

# Specific health groups
curl http://localhost:8080/actuator/health/readiness
curl http://localhost:8080/actuator/health/liveness
```

#### Container Health Checks

Docker containers include built-in health checks:

```bash
# Check container health
docker ps
docker inspect spring-drools-integration --format='{{.State.Health.Status}}'

# Using management script
./scripts/manage.sh health
```

### Monitoring Stack

#### Prometheus Metrics

- **Endpoint**: http://localhost:9090
- **Metrics Path**: `/actuator/prometheus`
- **Custom Metrics**:
  - Rule execution time
  - Rule hit/miss statistics
  - Application performance metrics

#### Grafana Dashboards

- **Endpoint**: http://localhost:3000
- **Credentials**: admin/admin123
- **Dashboards**: Pre-configured for Spring Boot and Drools metrics

### Log Management

#### Log Locations

- **Container Logs**: `docker logs spring-drools-integration`
- **Application Logs**: `/app/logs/spring-drools-integration.log`
- **Volume Logs**: `app-logs` volume

#### Log Commands

```bash
# View container logs
./scripts/manage.sh logs

# Follow logs
./scripts/manage.sh logs 100 true

# Docker Compose logs
./scripts/deploy.sh development logs
```

## Security Considerations

### Container Security

1. **Non-root User**: Application runs as `appuser` (UID/GID 1001)
2. **Minimal Base Image**: Uses slim JRE image
3. **Security Updates**: Regular base image updates
4. **File Permissions**: Secure file and directory permissions

### Network Security

1. **Internal Network**: Services communicate via Docker network
2. **Port Exposure**: Only necessary ports exposed
3. **TLS/SSL**: Configure HTTPS in production

### Data Security

1. **Volume Encryption**: Consider encrypted volumes for sensitive data
2. **Secret Management**: Use Docker secrets or external secret management
3. **Database Security**: Secure database credentials and connections

### Production Security Checklist

- [ ] Use specific image tags (not `latest`)
- [ ] Implement proper secret management
- [ ] Configure TLS/SSL certificates
- [ ] Set up network policies
- [ ] Enable audit logging
- [ ] Regular security updates
- [ ] Vulnerability scanning

## Troubleshooting

### Common Issues

#### Container Won't Start

```bash
# Check container logs
./scripts/manage.sh logs

# Check Docker Compose logs
./scripts/deploy.sh development logs

# Verify prerequisites
docker info
docker-compose --version
```

#### Health Check Failures

```bash
# Check application health
./scripts/manage.sh health

# Check specific health indicators
curl http://localhost:8080/actuator/health/drools
curl http://localhost:8080/actuator/health/decisionTable
```

#### Database Connection Issues

```bash
# Check PostgreSQL container
docker logs spring-drools-postgres

# Test database connectivity
docker exec -it spring-drools-postgres psql -U ruleuser -d ruledb -c "SELECT 1;"
```

#### Performance Issues

```bash
# Check container resources
./scripts/manage.sh stats

# Monitor JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Debug Commands

```bash
# Container information
./scripts/manage.sh info

# Execute commands in container
./scripts/manage.sh exec "ps aux"
./scripts/manage.sh exec "df -h"
./scripts/manage.sh exec "free -m"

# Open interactive shell
./scripts/manage.sh shell
```

### Log Analysis

```bash
# Application startup logs
./scripts/manage.sh logs | grep "Started SpringDroolsIntegrationApplication"

# Error logs
./scripts/manage.sh logs | grep ERROR

# Health check logs
./scripts/manage.sh logs | grep "health"
```

## Production Deployment

### Pre-deployment Checklist

- [ ] Build and test Docker image
- [ ] Configure production environment variables
- [ ] Set up external database
- [ ] Configure monitoring and alerting
- [ ] Set up backup procedures
- [ ] Configure log aggregation
- [ ] Security review completed
- [ ] Load testing completed

### Production Configuration

#### Environment Variables

```bash
# Production environment
export SPRING_PROFILES_ACTIVE="prod,docker"
export SPRING_DATASOURCE_URL="jdbc:postgresql://prod-db:5432/ruledb_prod"
export SPRING_DATASOURCE_USERNAME="prod_user"
export SPRING_DATASOURCE_PASSWORD="secure_password"
export JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Xlog:gc:gc.log"
```

#### Resource Limits

```yaml
spring-drools-app:
  deploy:
    resources:
      limits:
        cpus: '2.0'
        memory: 2G
      reservations:
        cpus: '1.0'
        memory: 1G
```

#### Production Docker Compose

Create a `docker-compose.prod.yml` file:

```yaml
version: '3.8'
services:
  spring-drools-app:
    image: your-registry/spring-drools-integration:${VERSION}
    environment:
      - SPRING_PROFILES_ACTIVE=prod,docker
      - SPRING_DATASOURCE_URL=${DATABASE_URL}
      - SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
```

### Deployment Commands

```bash
# Production deployment
./scripts/deploy.sh production up

# Scale application
docker-compose -f docker-compose.prod.yml up -d --scale spring-drools-app=3

# Rolling update
docker-compose -f docker-compose.prod.yml up -d --no-deps spring-drools-app
```

### Monitoring and Alerting

#### Prometheus Alerts

Configure alerts for:
- Application health check failures
- High response times
- Memory usage thresholds
- Database connection issues

#### Log Monitoring

Set up log aggregation and monitoring for:
- Application errors
- Security events
- Performance metrics
- Business rule execution

### Backup and Recovery

#### Data Backup

```bash
# Backup application data
./scripts/manage.sh backup

# Database backup
docker exec spring-drools-postgres pg_dump -U ruleuser ruledb > backup.sql
```

#### Disaster Recovery

1. **Database Recovery**: Restore from database backups
2. **Application Data**: Restore from volume backups
3. **Configuration**: Version-controlled configuration files
4. **Monitoring**: Verify all services after recovery

### Maintenance

#### Regular Tasks

- [ ] Update base images monthly
- [ ] Review and rotate secrets quarterly
- [ ] Performance monitoring and optimization
- [ ] Security vulnerability scanning
- [ ] Backup verification
- [ ] Log rotation and cleanup

#### Update Procedure

```bash
# Build new version
./scripts/build.sh 1.1.0

# Deploy to staging
./scripts/deploy.sh staging up

# Run tests and validation

# Deploy to production
./scripts/deploy.sh production up
```

## Conclusion

This container deployment guide provides comprehensive instructions for deploying the Spring Boot Drools Integration application using Docker and Docker Compose. The configuration includes security best practices, monitoring, and production-ready features.

For additional support or questions, refer to the main README.md file or contact the development team.