# Monitoring Setup for Spring Drools Integration

This document provides instructions for setting up monitoring for the Spring Drools Integration application using Prometheus and Grafana.

## Overview

The monitoring setup consists of:

1. **Spring Boot Actuator**: Exposes metrics, health checks, and other monitoring endpoints from the application
2. **Prometheus**: Collects and stores metrics from the application
3. **Grafana**: Visualizes the metrics collected by Prometheus

## Prerequisites

- Docker and Docker Compose installed
- Spring Drools Integration application running

## Setup Instructions

### 1. Configure Spring Boot Application

The application is already configured with the necessary dependencies and settings:

- Spring Boot Actuator is included in the dependencies
- Prometheus endpoint is exposed at `/actuator/prometheus`
- Custom health indicators and metrics are implemented

### 2. Start Prometheus

1. Create a directory for Prometheus data:

```bash
mkdir -p prometheus/data
```

2. Create a Docker Compose file for Prometheus:

```bash
cat > docker-compose-prometheus.yml << EOF
version: '3'
services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - ./monitoring/prometheus/drools_rules.yml:/etc/prometheus/drools_rules.yml
      - ./prometheus/data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--web.enable-lifecycle'
    restart: unless-stopped
EOF
```

3. Start Prometheus:

```bash
docker-compose -f docker-compose-prometheus.yml up -d
```

4. Verify that Prometheus is running by accessing the web UI at http://localhost:9090

### 3. Start Grafana

1. Create a directory for Grafana data:

```bash
mkdir -p grafana/data
```

2. Create a Docker Compose file for Grafana:

```bash
cat > docker-compose-grafana.yml << EOF
version: '3'
services:
  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    volumes:
      - ./grafana/data:/var/lib/grafana
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    restart: unless-stopped
EOF
```

3. Start Grafana:

```bash
docker-compose -f docker-compose-grafana.yml up -d
```

4. Verify that Grafana is running by accessing the web UI at http://localhost:3000 (login with admin/admin)

### 4. Configure Grafana

1. Add Prometheus as a data source:
   - Go to Configuration > Data Sources
   - Click "Add data source"
   - Select "Prometheus"
   - Set the URL to http://prometheus:9090 (or http://localhost:9090 if not using Docker networking)
   - Click "Save & Test"

2. Import the Drools dashboard:
   - Go to Dashboards > Import
   - Click "Upload JSON file"
   - Select the `monitoring/grafana/drools-dashboard.json` file
   - Click "Import"

## Monitoring Features

### Health Checks

The application provides the following health checks:

- **Drools Rule Engine Health**: Checks if the rule engine is functioning properly
- **Decision Table Health**: Validates decision tables

Access health information at: http://localhost:8080/actuator/health

### Metrics

The application provides the following metrics:

- **Rule Execution Time**: Time taken to execute rules
- **Rule Hit/Miss Rate**: How often rules are triggered
- **Error Rate**: Rate of rule execution errors
- **System Metrics**: JVM memory, CPU usage, etc.

Access metrics information at: http://localhost:8080/actuator/metrics

### Custom Endpoints

The application provides a custom Actuator endpoint for rule metrics:

- **Rules Endpoint**: http://localhost:8080/actuator/rules
  - GET /actuator/rules - Get overall rule metrics
  - GET /actuator/rules/{ruleName} - Get metrics for a specific rule
  - GET /actuator/rules/status - Get rule engine status
  - POST /actuator/rules/reload - Reload rules
  - DELETE /actuator/rules - Reset rule metrics

## Alerting

Prometheus is configured with alert rules for:

- High rule execution time
- High error rate
- Low rule hit rate
- Rule engine down
- Decision table validation failures
- High JVM memory usage

To set up alerting with AlertManager:

1. Add AlertManager configuration to the Prometheus configuration
2. Configure notification channels (email, Slack, etc.)

## Dashboard Overview

The Grafana dashboard provides the following panels:

### Rule Engine Health
- Drools Rule Engine Status
- Decision Table Status

### Rule Execution Metrics
- Rule Execution Time
- Max Rule Execution Time
- Rule Hit Distribution
- Overall Rule Hit Rate

### Error Metrics
- Rule Error Rate
- Overall Error Rate

### System Metrics
- JVM Memory Usage
- CPU Usage
- HTTP Request Rate

## Troubleshooting

### Common Issues

1. **Prometheus can't scrape metrics**:
   - Ensure the application is running
   - Check that the `/actuator/prometheus` endpoint is accessible
   - Verify the Prometheus configuration has the correct target URL

2. **Grafana can't connect to Prometheus**:
   - Check the Prometheus data source configuration in Grafana
   - Ensure Prometheus is running and accessible

3. **Metrics not showing in Grafana**:
   - Verify that the application is generating metrics
   - Check the metric names in the Grafana dashboard match those exposed by the application
   - Ensure the time range in Grafana is appropriate

## Maintenance

### Backing Up Prometheus Data

```bash
docker-compose -f docker-compose-prometheus.yml stop prometheus
tar -cvzf prometheus-backup.tar.gz prometheus/data
docker-compose -f docker-compose-prometheus.yml start prometheus
```

### Backing Up Grafana Data

```bash
docker-compose -f docker-compose-grafana.yml stop grafana
tar -cvzf grafana-backup.tar.gz grafana/data
docker-compose -f docker-compose-grafana.yml start grafana
```

## References

- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Prometheus Documentation](https://prometheus.io/docs/introduction/overview/)
- [Grafana Documentation](https://grafana.com/docs/grafana/latest/)