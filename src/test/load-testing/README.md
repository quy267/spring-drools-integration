# Performance Testing Suite

This directory contains the performance testing suite for the Spring Drools Integration application. The suite includes JMeter test plans for all three rule scenarios and a script to automate test execution and analysis.

## Test Plans

The following JMeter test plans are available in the `jmeter` directory:

1. **customer-discount-test-plan.jmx** - Tests the Customer Discount Rules API
2. **loan-approval-test-plan.jmx** - Tests the Loan Approval Rules API
3. **product-recommendation-test-plan.jmx** - Tests the Product Recommendation Rules API

Each test plan includes multiple thread groups to test different API endpoints and usage patterns:

- Single request thread groups for testing individual rule executions
- Batch request thread groups for testing multiple rule executions at once
- Async request thread groups for testing asynchronous rule executions
- Statistics request thread groups for monitoring rule execution metrics

## Prerequisites

- Apache JMeter (5.5 or later recommended)
- Bash shell environment
- The Spring Drools Integration application running on localhost:8080 (or configured host/port)

## Running Performance Tests

### Using the Automated Script

The `run-performance-tests.sh` script automates the execution of JMeter test plans and collects performance metrics.

```bash
# Set JMeter home directory (if not in default location)
export JMETER_HOME=/path/to/apache-jmeter

# Make the script executable
chmod +x run-performance-tests.sh

# Run all test plans
./run-performance-tests.sh all

# Run a specific test plan (without .jmx extension)
./run-performance-tests.sh customer-discount-test-plan

# Analyze recent test results
./run-performance-tests.sh analyze

# Show usage information
./run-performance-tests.sh
```

### Manual Execution with JMeter GUI

1. Open JMeter GUI:
   ```bash
   $JMETER_HOME/bin/jmeter
   ```

2. Open a test plan file:
   - File > Open > Select a .jmx file from the `jmeter` directory

3. Configure test parameters if needed:
   - Modify the User Defined Variables for host, port, etc.
   - Adjust thread counts and loop counts as needed

4. Run the test:
   - Click the green "Start" button in the toolbar

5. View results:
   - Check the listeners (View Results Tree, Summary Report, Graph Results)

## Test Results

The `run-performance-tests.sh` script saves test results in the following directories:

- `results/` - Contains JTL result files and HTML dashboard reports
- `logs/` - Contains JMeter log files

Each test run creates timestamped files to avoid overwriting previous results.

## Analyzing Results

### Using the Automated Script

```bash
./run-performance-tests.sh analyze
```

This command analyzes the most recent test results and displays summary statistics including:
- Number of samples
- Average response time
- Minimum response time
- Maximum response time
- Number of errors

### Using JMeter Dashboard Reports

The script generates HTML dashboard reports for each test run. Open the `index.html` file in the dashboard directory to view detailed reports including:

- Response time statistics
- Throughput metrics
- Error rates
- Response time distribution
- Response time percentiles

### Key Performance Metrics

When analyzing test results, focus on these key metrics:

1. **Response Time**:
   - Average: Should be under 500ms for standard rule sets
   - 90th percentile: Should be under 1000ms
   - Maximum: Monitor for outliers

2. **Throughput**:
   - Transactions per second: Higher is better
   - Monitor how throughput changes under different loads

3. **Error Rate**:
   - Should be close to 0%
   - Any errors should be investigated

4. **Resource Utilization**:
   - CPU usage: Monitor for bottlenecks
   - Memory usage: Watch for memory leaks or excessive consumption
   - Database connection pool: Check for connection exhaustion

## Identifying Bottlenecks

Common performance bottlenecks in rule execution systems:

1. **Rule Compilation**:
   - High response times for first requests
   - Solution: Ensure rule compilation caching is working correctly

2. **Session Management**:
   - Slow response times under high concurrency
   - Solution: Optimize session pooling configuration

3. **Memory Usage**:
   - Increasing response times over time
   - Solution: Check for memory leaks, optimize large dataset handling

4. **Database Operations**:
   - High response times for database-dependent operations
   - Solution: Optimize connection pooling, add caching

5. **Rule Complexity**:
   - Specific rule scenarios taking longer than others
   - Solution: Optimize complex rules, consider rule partitioning

## Performance Optimization Recommendations

After identifying bottlenecks, consider these optimization strategies:

1. **Rule Execution Optimization**:
   - Increase rule session pool size for higher concurrency
   - Optimize rule compilation caching
   - Use stateless sessions for simple rule executions

2. **Memory Optimization**:
   - Process large datasets in chunks
   - Implement efficient data structures
   - Consider JVM tuning (heap size, garbage collection)

3. **Caching Strategies**:
   - Cache frequently executed rules
   - Cache common rule inputs and outputs
   - Tune cache eviction policies

4. **Database Optimization**:
   - Optimize connection pool settings
   - Add indexes for frequently queried fields
   - Consider read-only transactions for queries

5. **Concurrency Improvements**:
   - Use asynchronous processing for non-blocking operations
   - Implement proper thread pool configuration
   - Consider reactive programming for high-throughput scenarios

## Continuous Performance Testing

For ongoing performance monitoring:

1. Integrate performance tests into CI/CD pipeline
2. Set performance thresholds for automated test success/failure
3. Compare performance metrics across builds to identify regressions
4. Maintain a performance testing dashboard for trend analysis

## Troubleshooting

Common issues and solutions:

1. **JMeter not found error**:
   - Set the JMETER_HOME environment variable correctly
   - Ensure JMeter is installed and executable

2. **Connection refused errors**:
   - Verify the application is running
   - Check host and port configuration in test plans

3. **Out of memory errors in JMeter**:
   - Increase JMeter heap size in jmeter.bat/jmeter.sh
   - Reduce thread counts or sample data size

4. **Dashboard generation errors**:
   - Ensure JMeter has write permissions to the results directory
   - Check for disk space issues