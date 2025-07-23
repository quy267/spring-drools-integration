# Operations Runbook - Spring Boot Drools Integration

## Overview

This operations runbook provides comprehensive guidance for deploying, monitoring, and maintaining the Spring Boot Drools Integration application in production environments.

## Table of Contents

1. [Application Overview](#application-overview)
2. [Deployment Procedures](#deployment-procedures)
3. [Monitoring and Alerting](#monitoring-and-alerting)
4. [Troubleshooting Guide](#troubleshooting-guide)
5. [Maintenance Procedures](#maintenance-procedures)
6. [Emergency Procedures](#emergency-procedures)
7. [Performance Tuning](#performance-tuning)
8. [Security Operations](#security-operations)

## Application Overview

### Architecture
- **Framework**: Spring Boot 3.5.3 with Java 17
- **Rule Engine**: Drools 8.44.0.Final
- **Database**: H2 (development), PostgreSQL/MySQL (production)
- **Monitoring**: Prometheus + Grafana
- **Containerization**: Docker with multi-stage builds

### Key Components
- **Rule Execution Engine**: Processes business rules using Drools
- **Decision Table Processor**: Handles Excel-based rule definitions
- **Health Indicators**: Custom health checks for rule engine components
- **Metrics Collection**: Custom metrics for rule execution performance
- **API Layer**: RESTful endpoints for rule execution and management

## Deployment Procedures

### 1. Pre-Deployment Checklist

#### Environment Verification
```bash
# Verify Java version
java -version  # Should be Java 17+

# Check available memory
free -h

# Verify disk space
df -h

# Check network connectivity
ping -c 3 database-host
ping -c 3 monitoring-host
```

#### Configuration Validation
```bash
# Validate application properties
grep -E "^(spring\.|app\.|management\.)" /app/config/application-prod.properties

# Check environment variables
env | grep -E "^(SPRING_|APP_|DB_)"

# Verify SSL certificates
openssl x509 -in /app/keystore/drools-app.p12 -text -noout
```

### 2. Deployment Steps

#### Standard Deployment
```bash
# 1. Stop current application
systemctl stop spring-drools-integration

# 2. Backup current version
cp /app/spring-drools-integration.jar /backup/app/spring-drools-integration-$(date +%Y%m%d_%H%M%S).jar

# 3. Deploy new version
cp /deploy/spring-drools-integration.jar /app/

# 4. Update permissions
chown app:app /app/spring-drools-integration.jar
chmod 755 /app/spring-drools-integration.jar

# 5. Start application
systemctl start spring-drools-integration

# 6. Verify deployment
curl -k https://localhost:8443/actuator/health
```

#### Docker Deployment
```bash
# 1. Pull new image
docker pull your-registry/spring-drools-integration:latest

# 2. Stop current container
docker stop spring-drools-integration

# 3. Remove old container
docker rm spring-drools-integration

# 4. Start new container
docker run -d \
  --name spring-drools-integration \
  --restart unless-stopped \
  -p 8443:8443 \
  -p 8080:8080 \
  -v /app/config:/app/config \
  -v /app/rules:/app/rules \
  -v /app/logs:/app/logs \
  your-registry/spring-drools-integration:latest

# 5. Verify deployment
docker logs spring-drools-integration
curl -k https://localhost:8443/actuator/health
```

### 3. Post-Deployment Verification

#### Health Checks
```bash
# Application health
curl -k https://localhost:8443/actuator/health

# Drools engine health
curl -k https://localhost:8443/actuator/health/drools

# Decision table health
curl -k https://localhost:8443/actuator/health/decisionTable

# Database connectivity
curl -k https://localhost:8443/actuator/health/db
```

#### Functional Testing
```bash
# Test rule execution
curl -k -X POST https://localhost:8443/api/v1/rules/execute \
  -H "Content-Type: application/json" \
  -d '{"customer": {"age": 25, "loyaltyTier": "GOLD"}, "order": {"amount": 1000}}'

# Test rule management
curl -k https://localhost:8443/api/v1/rules/metadata

# Test metrics endpoint
curl -k https://localhost:8443/actuator/metrics
```

## Monitoring and Alerting

### 1. Key Metrics to Monitor

#### Application Metrics
- **JVM Memory Usage**: `jvm.memory.used` / `jvm.memory.max`
- **CPU Usage**: `system.cpu.usage`
- **Thread Count**: `jvm.threads.live`
- **Garbage Collection**: `jvm.gc.pause`

#### Rule Engine Metrics
- **Rule Execution Time**: `drools.rule.execution.time`
- **Rule Success Rate**: `drools.rule.success.rate`
- **Session Pool Utilization**: `drools.session.pool.utilization`
- **Decision Table Load Time**: `drools.decision.table.load.time`

#### HTTP Metrics
- **Request Rate**: `http.server.requests.rate`
- **Response Time**: `http.server.requests.duration`
- **Error Rate**: `http.server.requests.errors`

### 2. Alert Thresholds

#### Critical Alerts
```yaml
# Application Down
- alert: ApplicationDown
  expr: up{job="spring-drools-integration"} == 0
  for: 1m

# High Memory Usage
- alert: HighMemoryUsage
  expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
  for: 5m

# Rule Execution Failures
- alert: RuleExecutionFailures
  expr: rate(drools_rule_execution_errors_total[5m]) > 0.1
  for: 2m
```

#### Warning Alerts
```yaml
# High Response Time
- alert: HighResponseTime
  expr: histogram_quantile(0.95, http_server_requests_duration_seconds) > 0.5
  for: 5m

# Session Pool High Utilization
- alert: SessionPoolHighUtilization
  expr: drools_session_pool_utilization > 0.8
  for: 5m
```

### 3. Monitoring Commands

#### Real-time Monitoring
```bash
# Monitor application logs
tail -f /app/logs/spring-drools-integration.log

# Monitor system resources
htop

# Monitor network connections
netstat -tulpn | grep :8443

# Monitor disk usage
watch df -h
```

#### Performance Monitoring
```bash
# JVM monitoring
jstat -gc -t $(pgrep java) 5s

# Thread dump
jstack $(pgrep java) > /tmp/thread-dump-$(date +%Y%m%d_%H%M%S).txt

# Heap dump
jmap -dump:format=b,file=/tmp/heap-dump-$(date +%Y%m%d_%H%M%S).hprof $(pgrep java)
```

## Troubleshooting Guide

### 1. Application Won't Start

#### Check Logs
```bash
# System logs
journalctl -u spring-drools-integration -f

# Application logs
tail -100 /app/logs/spring-drools-integration.log

# Docker logs (if containerized)
docker logs spring-drools-integration
```

#### Common Issues
- **Port Already in Use**: `netstat -tulpn | grep :8443`
- **Insufficient Memory**: Check JVM heap settings
- **Missing Configuration**: Verify application.properties
- **Database Connection**: Test database connectivity

### 2. High Memory Usage

#### Investigation Steps
```bash
# Check JVM memory usage
jstat -gc $(pgrep java)

# Generate heap dump
jmap -dump:format=b,file=/tmp/heapdump.hprof $(pgrep java)

# Analyze memory usage
jmap -histo $(pgrep java) | head -20
```

#### Resolution
- Increase JVM heap size: `-Xmx4g`
- Tune garbage collection: `-XX:+UseG1GC`
- Check for memory leaks in rule sessions
- Review rule complexity and data volume

### 3. Rule Execution Failures

#### Diagnosis
```bash
# Check rule engine health
curl -k https://localhost:8443/actuator/health/drools

# Review rule execution logs
grep "RuleExecutionException" /app/logs/spring-drools-integration.log

# Check decision table status
curl -k https://localhost:8443/actuator/health/decisionTable
```

#### Common Causes
- Malformed decision tables
- Rule compilation errors
- Session pool exhaustion
- Data validation failures

### 4. Performance Issues

#### Performance Analysis
```bash
# Check response times
curl -w "@curl-format.txt" -k https://localhost:8443/api/v1/rules/execute

# Monitor active threads
jstack $(pgrep java) | grep -A 5 "RuleExecutionService"

# Check database performance
# PostgreSQL
SELECT * FROM pg_stat_activity WHERE state = 'active';

# MySQL
SHOW PROCESSLIST;
```

#### Optimization Steps
- Enable rule session pooling
- Optimize decision table structure
- Add database indexes
- Tune connection pool settings

## Maintenance Procedures

### 1. Regular Maintenance Tasks

#### Daily Tasks
```bash
# Check application health
curl -k https://localhost:8443/actuator/health

# Review error logs
grep -i error /app/logs/spring-drools-integration.log | tail -10

# Check disk space
df -h /app /backup

# Verify backup completion
ls -la /backup/rules/$(date +%Y%m%d)*
```

#### Weekly Tasks
```bash
# Rotate logs
logrotate /etc/logrotate.d/spring-drools-integration

# Clean old backups
find /backup -type f -mtime +30 -delete

# Update rule statistics
curl -k https://localhost:8443/actuator/metrics/drools.rule.execution.count

# Performance review
curl -k https://localhost:8443/actuator/metrics | grep -E "(response|execution)\.time"
```

#### Monthly Tasks
```bash
# Security updates
yum update -y  # or apt update && apt upgrade

# Certificate renewal check
openssl x509 -in /app/keystore/drools-app.p12 -text -noout | grep "Not After"

# Performance baseline review
# Compare current metrics with previous month

# Capacity planning review
# Analyze growth trends and resource usage
```

### 2. Rule Management

#### Rule Deployment
```bash
# Backup current rules
cp -r /app/rules /backup/rules/$(date +%Y%m%d_%H%M%S)

# Deploy new rules
cp /deploy/rules/* /app/rules/

# Validate rules
curl -k -X POST https://localhost:8443/api/v1/rules/validate

# Hot reload rules (if enabled)
curl -k -X POST https://localhost:8443/api/v1/rules/reload
```

#### Rule Rollback
```bash
# List available backups
ls -la /backup/rules/

# Rollback to previous version
BACKUP_DATE="20240723_105500"
rm -rf /app/rules/*
cp -r /backup/rules/$BACKUP_DATE/* /app/rules/

# Reload rules
curl -k -X POST https://localhost:8443/api/v1/rules/reload
```

## Emergency Procedures

### 1. Application Crash Recovery

#### Immediate Actions
```bash
# Check if process is running
pgrep -f spring-drools-integration

# Restart application
systemctl restart spring-drools-integration

# Verify restart
curl -k https://localhost:8443/actuator/health

# Check logs for crash cause
tail -100 /app/logs/spring-drools-integration.log
```

### 2. Database Connection Loss

#### Recovery Steps
```bash
# Test database connectivity
telnet $DB_HOST $DB_PORT

# Check database status
# PostgreSQL
pg_isready -h $DB_HOST -p $DB_PORT

# MySQL
mysqladmin -h $DB_HOST -P $DB_PORT ping

# Restart application if database is available
systemctl restart spring-drools-integration
```

### 3. High Load Situation

#### Load Reduction
```bash
# Enable rate limiting (if not already enabled)
# Update application-prod.properties:
# app.security.rate-limit.enabled=true
# app.security.rate-limit.max-requests=50

# Scale horizontally (if load balancer available)
# Deploy additional instances

# Monitor resource usage
htop
iostat -x 1
```

## Performance Tuning

### 1. JVM Tuning

#### Memory Settings
```bash
# Production JVM settings
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

#### Garbage Collection Tuning
```bash
# G1GC settings for low latency
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:+G1UseAdaptiveIHOP
```

### 2. Application Tuning

#### Connection Pool Settings
```properties
# HikariCP optimization
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
```

#### Rule Engine Optimization
```properties
# Drools session pool
app.drools.session-pool-size=20
app.drools.session-timeout=300000
app.drools.max-rules-per-session=1000
```

## Security Operations

### 1. Security Monitoring

#### Log Analysis
```bash
# Monitor failed authentication attempts
grep "authentication failed" /app/logs/spring-drools-integration.log

# Check for suspicious API calls
grep -E "(POST|PUT|DELETE)" /app/logs/spring-drools-integration.log | grep -v "200\|201"

# Monitor file upload attempts
grep "file upload" /app/logs/spring-drools-integration.log
```

### 2. Certificate Management

#### Certificate Renewal
```bash
# Check certificate expiration
openssl x509 -in /app/keystore/drools-app.p12 -text -noout | grep "Not After"

# Generate new certificate (example)
keytool -genkeypair -alias drools-app -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore /app/keystore/drools-app.p12 \
  -validity 365 -dname "CN=drools-app"

# Restart application after certificate update
systemctl restart spring-drools-integration
```

## Contact Information

### Operations Team
- **Primary On-Call**: +1-555-0123
- **Secondary On-Call**: +1-555-0124
- **Email**: ops-team@company.com
- **Slack**: #ops-alerts

### Development Team
- **Lead Developer**: dev-lead@company.com
- **Team Email**: dev-team@company.com
- **Slack**: #dev-support

### Vendor Support
- **Database Support**: db-support@vendor.com
- **Infrastructure Support**: infra-support@vendor.com
- **Monitoring Support**: monitoring-support@vendor.com

## Appendix

### A. Configuration Files Locations
- **Application Config**: `/app/config/application*.properties`
- **SSL Certificates**: `/app/keystore/`
- **Rule Files**: `/app/rules/`
- **Log Files**: `/app/logs/`
- **Backup Location**: `/backup/`

### B. Important URLs
- **Application Health**: `https://localhost:8443/actuator/health`
- **Metrics**: `https://localhost:8443/actuator/metrics`
- **API Documentation**: `https://localhost:8443/swagger-ui.html`
- **Grafana Dashboard**: `http://monitoring-host:3000`
- **Prometheus**: `http://monitoring-host:9090`

### C. Common Commands Reference
```bash
# Application management
systemctl start spring-drools-integration
systemctl stop spring-drools-integration
systemctl restart spring-drools-integration
systemctl status spring-drools-integration

# Log monitoring
tail -f /app/logs/spring-drools-integration.log
journalctl -u spring-drools-integration -f

# Health checks
curl -k https://localhost:8443/actuator/health
curl -k https://localhost:8443/actuator/health/drools

# Rule management
curl -k -X POST https://localhost:8443/api/v1/rules/reload
curl -k https://localhost:8443/api/v1/rules/metadata
```

---

**Last Updated**: 2024-07-23  
**Version**: 1.0  
**Owner**: Operations Team  
**Review Date**: 2024-10-23