# Troubleshooting Guide - Spring Boot Drools Integration

This comprehensive troubleshooting guide helps you diagnose and resolve common issues with the Spring Boot Drools Integration application.

## 📋 Table of Contents

- [Quick Diagnosis](#quick-diagnosis)
- [Application Startup Issues](#application-startup-issues)
- [Rule Execution Problems](#rule-execution-problems)
- [Decision Table Issues](#decision-table-issues)
- [API and HTTP Issues](#api-and-http-issues)
- [Performance Problems](#performance-problems)
- [Security and Authentication Issues](#security-and-authentication-issues)
- [Docker and Deployment Issues](#docker-and-deployment-issues)
- [Logging and Monitoring Issues](#logging-and-monitoring-issues)
- [Database and Persistence Issues](#database-and-persistence-issues)
- [Memory and Resource Issues](#memory-and-resource-issues)
- [Debugging Tools and Techniques](#debugging-tools-and-techniques)
- [Common Error Messages](#common-error-messages)
- [Getting Help](#getting-help)

## 🔍 Quick Diagnosis

### Health Check Commands

Start troubleshooting with these quick health checks:

```bash
# Check if application is running
curl -f http://localhost:8080/actuator/health

# Check application info
curl http://localhost:8080/actuator/info

# Check metrics
curl http://localhost:8080/actuator/metrics

# Check logs
tail -f logs/application.log

# Check Java process
jps -l | grep spring-drools-integration
```

### Common Symptoms and Quick Fixes

| Symptom | Quick Check | Quick Fix |
|---------|-------------|-----------|
| App won't start | Check Java version: `java -version` | Install Java 17+ |
| Port already in use | Check port: `netstat -tulpn \| grep :8080` | Kill process or change port |
| Out of memory | Check memory: `free -h` | Increase heap size |
| Rules not working | Check decision tables exist | Verify file paths |
| API returns 401 | Check credentials | Use admin/admin123 |
| Slow performance | Check CPU/memory usage | Optimize JVM settings |

## 🚀 Application Startup Issues

### Issue: Application Fails to Start

**Symptoms:**
- Application exits immediately
- "Failed to start" error messages
- Port binding errors

**Common Causes and Solutions:**

#### 1. Java Version Issues

```bash
# Check Java version
java -version

# Should show Java 17 or higher
# If not, install correct version:
sudo apt update
sudo apt install openjdk-17-jdk

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

#### 2. Port Already in Use

```bash
# Check what's using port 8080
sudo netstat -tulpn | grep :8080
sudo lsof -i :8080

# Kill the process
sudo kill -9 <PID>

# Or change application port
java -jar app.jar --server.port=8081
```

#### 3. Missing Dependencies

```bash
# Rebuild with dependencies
mvn clean package

# Check for missing JARs
java -cp target/lib/*:target/spring-drools-integration-1.0.0.jar com.example.springdroolsintegration.SpringDroolsIntegrationApplication
```

#### 4. Configuration Errors

```bash
# Test with minimal configuration
java -jar app.jar --spring.profiles.active=dev --logging.level.root=DEBUG

# Check configuration files
ls -la src/main/resources/application*.properties

# Validate YAML syntax (if using YAML)
python -c "import yaml; yaml.safe_load(open('application.yml'))"
```

### Issue: Slow Startup

**Symptoms:**
- Application takes more than 60 seconds to start
- "Timed out waiting for application to start" messages

**Solutions:**

```bash
# Increase startup timeout
export SPRING_BOOT_STARTUP_TIMEOUT=120

# Optimize JVM startup
export JAVA_OPTS="-XX:TieredStopAtLevel=1 -noverify"

# Enable parallel GC
export JAVA_OPTS="$JAVA_OPTS -XX:+UseParallelGC"

# Check for slow beans
java -jar app.jar --debug --logging.level.org.springframework.boot.autoconfigure=DEBUG
```

## ⚙️ Rule Execution Problems

### Issue: Rules Not Executing

**Symptoms:**
- API calls return default values
- No rule execution logs
- Expected business logic not applied

**Diagnostic Steps:**

```bash
# Check rule engine status
curl -u admin:admin123 http://localhost:8080/api/v1/rules/status

# Validate rules
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/rules/validate

# Check rule loading logs
grep -i "rule" logs/application.log | tail -20

# Enable rule execution logging
export LOGGING_LEVEL_ORG_DROOLS=DEBUG
```

**Common Solutions:**

#### 1. Decision Tables Not Found

```bash
# Check decision table location
ls -la src/main/resources/rules/decision-tables/

# Verify configuration
grep -i "decision-table-path" src/main/resources/application.properties

# Check file permissions
chmod 644 src/main/resources/rules/decision-tables/*.xlsx
```

#### 2. Rule Compilation Errors

```bash
# Check for compilation errors in logs
grep -i "error\|exception" logs/application.log | grep -i "rule\|drools"

# Validate Excel file format
file src/main/resources/rules/decision-tables/*.xlsx

# Test with simple rule
curl -X POST http://localhost:8080/api/v1/rules/execute \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{"rulePackage":"test","facts":[]}'
```

#### 3. Fact Insertion Issues

```bash
# Enable fact insertion logging
export LOGGING_LEVEL_COM_EXAMPLE_SPRINGDROOLSINTEGRATION_SERVICE=DEBUG

# Check fact types match rule conditions
# Verify JSON structure matches expected objects
```

### Issue: Incorrect Rule Results

**Symptoms:**
- Rules execute but return wrong results
- Unexpected rule combinations firing
- Missing expected rule executions

**Debugging Steps:**

```bash
# Enable rule tracing
export DROOLS_RULE_TRACING=true

# Check rule priorities (salience)
# Higher salience rules should execute first

# Verify rule conditions
# Check for typos in condition syntax

# Test individual rules
# Isolate rules by commenting out others in Excel
```

## 📊 Decision Table Issues

### Issue: Decision Table Loading Errors

**Symptoms:**
- "Failed to load decision table" errors
- Rules not updating after Excel changes
- Excel file format errors

**Solutions:**

#### 1. File Format Issues

```bash
# Check file format
file src/main/resources/rules/decision-tables/*.xlsx

# Convert to correct format if needed
libreoffice --headless --convert-to xlsx *.xls

# Verify Excel structure
# - Must have proper headers
# - RuleSet, RuleId, Salience columns required
# - No merged cells in rule area
```

#### 2. Permission Issues

```bash
# Check file permissions
ls -la src/main/resources/rules/decision-tables/

# Fix permissions
chmod 644 src/main/resources/rules/decision-tables/*.xlsx
chown $USER:$USER src/main/resources/rules/decision-tables/*.xlsx
```

#### 3. Hot Reload Issues

```bash
# Check hot reload configuration
grep -i "hot-reload" src/main/resources/application.properties

# Manually trigger reload
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/rules/reload

# Check file watching logs
grep -i "file.*watch\|reload" logs/application.log
```

### Issue: Rule Syntax Errors

**Symptoms:**
- Rule compilation failures
- Syntax error messages in logs
- Rules not executing as expected

**Common Syntax Issues:**

```excel
# Incorrect condition syntax
age > 60        # Correct
age greater 60  # Incorrect

# String comparison issues
loyaltyTier == "GOLD"    # Correct
loyaltyTier == GOLD      # Incorrect (missing quotes)

# Numeric comparison issues
orderAmount >= 100.0     # Correct
orderAmount >= $100      # Incorrect (currency symbol)

# Boolean logic issues
age > 60 && tier == "GOLD"    # Use in separate columns
age > 60 AND tier == "GOLD"   # Incorrect syntax
```

## 🌐 API and HTTP Issues

### Issue: HTTP 401 Unauthorized

**Symptoms:**
- API calls return 401 status
- "Unauthorized" error messages
- Authentication failures

**Solutions:**

```bash
# Check default credentials
curl -u admin:admin123 http://localhost:8080/api/v1/rules/status

# Verify credentials in configuration
grep -i "security.user" src/main/resources/application.properties

# Check authentication header format
# Correct: Authorization: Basic YWRtaW46YWRtaW4xMjM=
# Base64 encode "admin:admin123"
echo -n "admin:admin123" | base64
```

### Issue: HTTP 404 Not Found

**Symptoms:**
- API endpoints return 404
- "No mapping found" errors
- Incorrect URL paths

**Solutions:**

```bash
# Check available endpoints
curl -u admin:admin123 http://localhost:8080/actuator/mappings

# Verify context path
grep -i "context-path" src/main/resources/application.properties

# Check controller mappings
grep -r "@RequestMapping\|@GetMapping\|@PostMapping" src/main/java/
```

### Issue: HTTP 500 Internal Server Error

**Symptoms:**
- Server errors on API calls
- Exception stack traces in logs
- Unexpected application behavior

**Debugging Steps:**

```bash
# Check recent error logs
tail -50 logs/application.log | grep -i "error\|exception"

# Enable debug logging for controllers
export LOGGING_LEVEL_COM_EXAMPLE_SPRINGDROOLSINTEGRATION_CONTROLLER=DEBUG

# Check request/response logging
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG

# Test with simple request
curl -X GET -u admin:admin123 http://localhost:8080/actuator/health
```

## 🚀 Performance Problems

### Issue: Slow API Response Times

**Symptoms:**
- API calls take more than 5 seconds
- Timeout errors
- High CPU usage

**Performance Analysis:**

```bash
# Check response times
time curl -u admin:admin123 http://localhost:8080/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -d '{"customerId":"TEST","age":30,"loyaltyTier":"GOLD","orderAmount":100}'

# Monitor JVM performance
jstat -gc -t $(jps -l | grep spring-drools | cut -d' ' -f1) 5s

# Check thread usage
jstack $(jps -l | grep spring-drools | cut -d' ' -f1) | grep -A5 -B5 "BLOCKED\|WAITING"

# Monitor memory usage
jmap -histo $(jps -l | grep spring-drools | cut -d' ' -f1) | head -20
```

**Optimization Solutions:**

```bash
# Increase thread pool size
export SERVER_TOMCAT_MAX_THREADS=200

# Optimize JVM settings
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Enable connection pooling
export SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20

# Optimize rule execution
# - Reduce rule complexity
# - Optimize rule order (salience)
# - Cache frequently used results
```

### Issue: High Memory Usage

**Symptoms:**
- OutOfMemoryError exceptions
- Frequent garbage collection
- Application becomes unresponsive

**Memory Analysis:**

```bash
# Check memory usage
free -h
ps aux | grep java

# Generate heap dump
jcmd $(jps -l | grep spring-drools | cut -d' ' -f1) GC.run_finalization
jcmd $(jps -l | grep spring-drools | cut -d' ' -f1) VM.gc
jmap -dump:format=b,file=heapdump.hprof $(jps -l | grep spring-drools | cut -d' ' -f1)

# Analyze with Eclipse MAT or VisualVM
# Look for memory leaks, large objects, retained objects
```

**Memory Optimization:**

```bash
# Increase heap size
export JAVA_OPTS="-Xmx4g -Xms2g"

# Optimize garbage collection
export JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Enable memory monitoring
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"

# Optimize rule sessions
# - Use stateless sessions when possible
# - Clear working memory after rule execution
# - Avoid retaining large fact objects
```

## 🔒 Security and Authentication Issues

### Issue: Authentication Failures

**Symptoms:**
- Cannot access protected endpoints
- Invalid credentials errors
- Security configuration issues

**Solutions:**

```bash
# Check security configuration
grep -r "@EnableWebSecurity\|@Configuration" src/main/java/ -A10

# Verify user configuration
grep -i "spring.security.user" src/main/resources/application*.properties

# Test with curl
curl -v -u admin:admin123 http://localhost:8080/actuator/health

# Check security logs
grep -i "security\|auth" logs/application.log | tail -20
```

### Issue: CORS Errors

**Symptoms:**
- Browser console shows CORS errors
- Cross-origin requests blocked
- Preflight request failures

**Solutions:**

```java
// Add CORS configuration
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Issue: SSL/TLS Problems

**Symptoms:**
- HTTPS connection failures
- Certificate errors
- SSL handshake failures

**Solutions:**

```bash
# Check SSL configuration
grep -i "ssl\|https" src/main/resources/application.properties

# Test SSL connection
openssl s_client -connect localhost:8443 -servername localhost

# Check certificate validity
keytool -list -v -keystore keystore.p12 -storepass password

# Generate self-signed certificate for testing
keytool -genkeypair -alias drools-app -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 365
```

## 🐳 Docker and Deployment Issues

### Issue: Docker Container Won't Start

**Symptoms:**
- Container exits immediately
- Docker build failures
- Container health check failures

**Debugging Steps:**

```bash
# Check container logs
docker logs container-name

# Run container interactively
docker run -it --entrypoint /bin/bash spring-drools-integration:1.0.0

# Check container resources
docker stats container-name

# Inspect container configuration
docker inspect container-name
```

**Common Solutions:**

```bash
# Fix permission issues
docker run --user $(id -u):$(id -g) spring-drools-integration:1.0.0

# Increase memory limits
docker run --memory=2g spring-drools-integration:1.0.0

# Check port mapping
docker run -p 8080:8080 spring-drools-integration:1.0.0

# Debug startup issues
docker run -e JAVA_OPTS="-Xmx1g" -e LOGGING_LEVEL_ROOT=DEBUG spring-drools-integration:1.0.0
```

### Issue: Docker Build Failures

**Symptoms:**
- Docker build command fails
- Dependency download errors
- File not found errors

**Solutions:**

```bash
# Clean build
docker system prune -a
docker build --no-cache -t spring-drools-integration:1.0.0 .

# Check Dockerfile syntax
docker build --dry-run -t spring-drools-integration:1.0.0 .

# Build with verbose output
docker build --progress=plain -t spring-drools-integration:1.0.0 .

# Check base image availability
docker pull openjdk:17-jre-slim
```

### Issue: Kubernetes Deployment Problems

**Symptoms:**
- Pods stuck in pending state
- CrashLoopBackOff errors
- Service not accessible

**Debugging Steps:**

```bash
# Check pod status
kubectl get pods -l app=drools-app

# Describe pod for events
kubectl describe pod <pod-name>

# Check pod logs
kubectl logs <pod-name> --previous

# Check service endpoints
kubectl get endpoints drools-service

# Test service connectivity
kubectl port-forward service/drools-service 8080:80
```

## 📊 Logging and Monitoring Issues

### Issue: Missing or Insufficient Logs

**Symptoms:**
- No log files generated
- Important events not logged
- Log level too high

**Solutions:**

```bash
# Check log configuration
grep -i "logging" src/main/resources/application.properties

# Set appropriate log levels
export LOGGING_LEVEL_COM_EXAMPLE_SPRINGDROOLSINTEGRATION=DEBUG
export LOGGING_LEVEL_ORG_DROOLS=INFO

# Check log file location
ls -la logs/
ls -la /var/log/drools/

# Verify log file permissions
chmod 644 logs/application.log
chown $USER:$USER logs/application.log
```

### Issue: Actuator Endpoints Not Working

**Symptoms:**
- /actuator endpoints return 404
- Health checks failing
- Metrics not available

**Solutions:**

```bash
# Check actuator configuration
grep -i "management.endpoints" src/main/resources/application.properties

# Enable all endpoints (development only)
export MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=*

# Check actuator dependency
grep -i "spring-boot-starter-actuator" pom.xml

# Test basic health endpoint
curl http://localhost:8080/actuator/health
```

## 💾 Database and Persistence Issues

### Issue: Database Connection Failures

**Symptoms:**
- Cannot connect to database
- Connection timeout errors
- SQL exceptions

**Solutions:**

```bash
# Check database configuration
grep -i "datasource" src/main/resources/application.properties

# Test database connectivity
telnet database-host 5432
nc -zv database-host 5432

# Check database credentials
mysql -h database-host -u username -p database-name
psql -h database-host -U username -d database-name

# Verify JDBC driver
grep -i "jdbc" pom.xml
```

### Issue: JPA/Hibernate Problems

**Symptoms:**
- Entity mapping errors
- SQL generation issues
- Transaction problems

**Solutions:**

```bash
# Enable SQL logging
export LOGGING_LEVEL_ORG_HIBERNATE_SQL=DEBUG
export LOGGING_LEVEL_ORG_HIBERNATE_TYPE_DESCRIPTOR_SQL_BASICBINDER=TRACE

# Check entity mappings
grep -r "@Entity\|@Table" src/main/java/

# Validate database schema
# Compare entity definitions with actual database tables

# Check transaction configuration
grep -r "@Transactional" src/main/java/
```

## 🧠 Memory and Resource Issues

### Issue: OutOfMemoryError

**Symptoms:**
- Application crashes with OOM
- "Java heap space" errors
- "GC overhead limit exceeded"

**Analysis and Solutions:**

```bash
# Analyze heap dump
jmap -dump:format=b,file=heap.hprof $(jps -l | grep spring-drools | cut -d' ' -f1)

# Check memory usage patterns
jstat -gc -t $(jps -l | grep spring-drools | cut -d' ' -f1) 5s 10

# Increase heap size
export JAVA_OPTS="-Xmx4g -Xms2g"

# Optimize garbage collection
export JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Enable OOM dump
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"
```

### Issue: High CPU Usage

**Symptoms:**
- CPU usage consistently above 80%
- Application becomes unresponsive
- Slow response times

**Analysis and Solutions:**

```bash
# Check CPU usage
top -p $(jps -l | grep spring-drools | cut -d' ' -f1)
htop

# Analyze thread dumps
jstack $(jps -l | grep spring-drools | cut -d' ' -f1) > thread-dump.txt

# Profile application
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=profile.jfr -jar app.jar

# Optimize thread pools
export SERVER_TOMCAT_MAX_THREADS=100
export SERVER_TOMCAT_MIN_SPARE_THREADS=10
```

## 🔧 Debugging Tools and Techniques

### JVM Debugging Tools

```bash
# Java process information
jps -l -v

# Memory analysis
jmap -histo <pid>
jmap -dump:format=b,file=heap.hprof <pid>

# Thread analysis
jstack <pid>
jcmd <pid> Thread.print

# GC analysis
jstat -gc -t <pid> 5s
jcmd <pid> GC.run

# Flight recorder
jcmd <pid> JFR.start duration=60s filename=recording.jfr
```

### Application Debugging

```bash
# Enable remote debugging
export JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Enable JMX
export JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999"

# Enable detailed logging
export LOGGING_LEVEL_ROOT=DEBUG
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=DEBUG
export LOGGING_LEVEL_ORG_DROOLS=DEBUG
```

### Network Debugging

```bash
# Check port connectivity
telnet localhost 8080
nc -zv localhost 8080

# Monitor network traffic
sudo tcpdump -i any port 8080
sudo netstat -tulpn | grep java

# Test HTTP endpoints
curl -v -u admin:admin123 http://localhost:8080/actuator/health
wget --debug --user=admin --password=admin123 http://localhost:8080/actuator/health
```

### Rule Engine Debugging

```bash
# Enable rule tracing
export DROOLS_RULE_TRACING=true
export LOGGING_LEVEL_ORG_DROOLS=DEBUG

# Check rule compilation
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/rules/validate

# Monitor rule execution
curl -u admin:admin123 http://localhost:8080/api/v1/rules/status

# Test individual rules
# Create minimal test cases
# Use rule debugger in IDE
```

## ❌ Common Error Messages

### "Port 8080 was already in use"

```bash
# Find process using port
sudo lsof -i :8080
sudo netstat -tulpn | grep :8080

# Kill process
sudo kill -9 <PID>

# Use different port
java -jar app.jar --server.port=8081
```

### "Unable to start embedded Tomcat"

```bash
# Check Java version
java -version

# Check available memory
free -h

# Check file descriptors
ulimit -n

# Increase if needed
ulimit -n 65536
```

### "Failed to load ApplicationContext"

```bash
# Check configuration files
ls -la src/main/resources/application*.properties

# Validate configuration syntax
# Check for typos in property names
# Verify required dependencies in pom.xml

# Enable debug logging
java -jar app.jar --debug
```

### "ClassNotFoundException" or "NoClassDefFoundError"

```bash
# Check classpath
java -cp target/lib/*:target/spring-drools-integration-1.0.0.jar -version

# Rebuild with dependencies
mvn clean package

# Check for version conflicts
mvn dependency:tree | grep -i conflict
```

### "OutOfMemoryError: Java heap space"

```bash
# Increase heap size
export JAVA_OPTS="-Xmx2g -Xms1g"

# Check for memory leaks
jmap -histo <pid> | head -20

# Enable heap dump on OOM
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
```

### "Connection refused" or "Connection timeout"

```bash
# Check if service is running
curl -f http://localhost:8080/actuator/health

# Check firewall rules
sudo iptables -L
sudo ufw status

# Check network connectivity
ping localhost
telnet localhost 8080
```

## 🆘 Getting Help

### Before Asking for Help

1. **Check this troubleshooting guide** for your specific issue
2. **Review application logs** for error messages and stack traces
3. **Test with minimal configuration** to isolate the problem
4. **Gather system information** (OS, Java version, memory, etc.)
5. **Document steps to reproduce** the issue

### Information to Include

When reporting issues, include:

```bash
# System information
uname -a
java -version
mvn -version
docker --version

# Application information
java -jar app.jar --version
curl http://localhost:8080/actuator/info

# Error logs (last 50 lines)
tail -50 logs/application.log

# Configuration (sanitized)
cat src/main/resources/application.properties | grep -v password

# Process information
ps aux | grep java
jps -l -v
```

### Log Collection Script

```bash
#!/bin/bash
# collect-logs.sh - Collect troubleshooting information

echo "=== System Information ===" > troubleshooting-info.txt
uname -a >> troubleshooting-info.txt
java -version 2>> troubleshooting-info.txt
free -h >> troubleshooting-info.txt

echo -e "\n=== Application Status ===" >> troubleshooting-info.txt
curl -s http://localhost:8080/actuator/health >> troubleshooting-info.txt 2>&1

echo -e "\n=== Recent Logs ===" >> troubleshooting-info.txt
tail -100 logs/application.log >> troubleshooting-info.txt

echo -e "\n=== Process Information ===" >> troubleshooting-info.txt
jps -l -v >> troubleshooting-info.txt

echo -e "\n=== Network Status ===" >> troubleshooting-info.txt
netstat -tulpn | grep :8080 >> troubleshooting-info.txt

echo "Troubleshooting information collected in troubleshooting-info.txt"
```

### Support Channels

1. **GitHub Issues**: Create detailed issue reports
2. **Documentation**: Check README and other docs
3. **Stack Overflow**: Tag questions with `spring-boot` and `drools`
4. **Community Forums**: Spring Boot and Drools communities

### Emergency Procedures

For critical production issues:

1. **Immediate Actions**:
   ```bash
   # Check application health
   curl -f http://localhost:8080/actuator/health
   
   # Check system resources
   free -h && df -h
   
   # Collect thread dump
   jstack $(jps -l | grep spring-drools | cut -d' ' -f1) > emergency-threads.txt
   ```

2. **Rollback Procedures**:
   ```bash
   # Stop current version
   sudo systemctl stop drools-app
   
   # Deploy previous version
   sudo cp /backup/spring-drools-integration-previous.jar /opt/drools/
   
   # Start previous version
   sudo systemctl start drools-app
   ```

3. **Escalation Path**:
   - Level 1: Application restart
   - Level 2: System administrator
   - Level 3: Development team
   - Level 4: Vendor support

---

**Remember**: Most issues can be resolved by carefully reading error messages, checking logs, and following systematic debugging approaches. When in doubt, start with the basics: Java version, configuration, and connectivity.**

**For additional support, please refer to the main [README](../README.md) or create an issue in the repository.**