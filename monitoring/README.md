# Performance Monitoring Setup for Spring Drools Integration

This document describes the performance monitoring setup for the Spring Drools Integration application, including how to use Prometheus and Grafana to monitor rule engine performance, set up alerts, and interpret the metrics.

## Overview

The monitoring setup consists of:

1. **Metrics Collection**: Spring Boot Actuator with Micrometer Prometheus registry
2. **Metrics Storage**: Prometheus time-series database
3. **Visualization**: Grafana dashboards
4. **Alerting**: Prometheus alert rules

## Metrics Collection

The application exposes metrics through Spring Boot Actuator's Prometheus endpoint at `/actuator/prometheus`. These metrics include:

- Rule execution counts and rates
- Rule execution times (with percentiles)
- Session pool statistics
- Cache performance metrics
- Memory usage and JVM metrics
- Custom business metrics for each rule type

## Prometheus Setup

### Configuration

Prometheus is configured to scrape metrics from the application every 5 seconds. The configuration is in `monitoring/prometheus/prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'spring-drools-integration'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['localhost:8080']
```

### Alert Rules

Alert rules are defined in `monitoring/prometheus/rules/alert_rules.yml` and include:

- **High Response Time Alerts**:
  - Warning: 95th percentile > 500ms for 2 minutes
  - Critical: 95th percentile > 1s for 1 minute

- **Error Rate Alerts**:
  - Warning: Error rate > 5% for 2 minutes
  - Critical: Error rate > 10% for 1 minute

- **Session Pool Alerts**:
  - Warning: Pool utilization > 80% for 5 minutes
  - Critical: Pool utilization > 95% for 2 minutes

- **Cache Performance Alerts**:
  - Warning: Cache hit ratio < 50% for 10 minutes

- **Memory Usage Alerts**:
  - Warning: JVM heap usage > 85% for 5 minutes
  - Critical: JVM heap usage > 95% for 2 minutes

- **Application Health Alerts**:
  - Critical: Application down for 1 minute
  - Critical: Drools health check failing for 1 minute

### Starting Prometheus

To start Prometheus with the configuration:

```bash
cd monitoring/prometheus
prometheus --config.file=prometheus.yml
```

Prometheus UI will be available at http://localhost:9090

## Grafana Setup

### Dashboard

A comprehensive dashboard is provided in `monitoring/grafana/rule_engine_dashboard.json`. The dashboard includes:

- **Overview Panels**:
  - Rule execution rate
  - Rule execution time (50th, 95th, 99th percentiles)
  - Success rate gauge
  - Session pool utilization gauge
  - Cache hit ratio gauge
  - JVM heap usage gauge

- **Detailed Metrics**:
  - Session creation/disposal rate
  - Session creation time
  - Cache statistics (hits, misses, size)
  - JVM memory usage (used, committed, max)

### Importing the Dashboard

1. Start Grafana
2. Go to Dashboards > Import
3. Upload the JSON file or paste its contents
4. Select the Prometheus data source
5. Click Import

Grafana will be available at http://localhost:3000

## Running the Monitoring Stack with Docker Compose

A Docker Compose file is provided to run the entire monitoring stack:

```bash
cd monitoring
docker-compose up -d
```

This will start:
- Prometheus on port 9090
- Grafana on port 3000
- Node Exporter for host metrics (if configured)

## Key Performance Indicators (KPIs)

The following KPIs should be monitored:

1. **Rule Execution Time**: Should be below 500ms for 95% of requests
2. **Success Rate**: Should be above 95%
3. **Session Pool Utilization**: Should be below 80% during normal operation
4. **Cache Hit Ratio**: Should be above 80% for optimal performance
5. **JVM Heap Usage**: Should be below 85% to avoid GC pressure

## Troubleshooting Common Performance Issues

### High Rule Execution Time

Possible causes:
- Complex rules with many conditions
- Large datasets being processed
- Insufficient caching
- JVM memory pressure

Solutions:
- Review and optimize rule complexity
- Use chunked batch processing for large datasets
- Increase cache size or improve caching strategy
- Increase JVM heap size or optimize memory usage

### Low Cache Hit Ratio

Possible causes:
- Cache size too small
- Highly variable input data
- Cache eviction happening too frequently

Solutions:
- Increase cache size
- Review caching strategy and keys
- Monitor cache evictions and adjust accordingly

### High Session Pool Utilization

Possible causes:
- High concurrent request volume
- Sessions not being returned to the pool
- Pool size too small

Solutions:
- Increase session pool size
- Check for session leaks
- Implement request throttling if necessary

### High Error Rate

Possible causes:
- Invalid rule definitions
- Data validation issues
- Resource constraints

Solutions:
- Review error logs for specific error types
- Validate rule definitions
- Check input data validation
- Increase resource allocation if necessary

## Extending the Monitoring

To add new metrics:

1. Add new counters, gauges, or timers in the `RuleEngineMetrics` class
2. Register them with the MeterRegistry
3. Update the Grafana dashboard to include the new metrics

To add new alerts:

1. Define new alert rules in `alert_rules.yml`
2. Restart Prometheus to load the new rules

## Conclusion

This monitoring setup provides comprehensive visibility into the performance of the Spring Drools Integration application, with a focus on rule engine performance. By monitoring the KPIs and responding to alerts, you can ensure optimal performance and reliability of the rule engine.