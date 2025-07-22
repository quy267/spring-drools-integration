#!/bin/bash

# Performance Test Runner Script
# This script automates the execution of JMeter test plans and collects performance metrics

# Configuration
JMETER_HOME=${JMETER_HOME:-"/opt/apache-jmeter"}
JMETER_BIN="${JMETER_HOME}/bin/jmeter"
TEST_PLANS_DIR="$(dirname "$0")/jmeter"
RESULTS_DIR="$(dirname "$0")/results"
LOG_DIR="$(dirname "$0")/logs"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create directories if they don't exist
mkdir -p "${RESULTS_DIR}"
mkdir -p "${LOG_DIR}"

# Check if JMeter is available
if [ ! -f "${JMETER_BIN}" ]; then
    echo "Error: JMeter not found at ${JMETER_BIN}"
    echo "Please install JMeter or set JMETER_HOME environment variable"
    exit 1
fi

# Function to run a test plan
run_test_plan() {
    local test_plan="$1"
    local test_name=$(basename "${test_plan}" .jmx)
    local result_file="${RESULTS_DIR}/${test_name}_${TIMESTAMP}.jtl"
    local log_file="${LOG_DIR}/${test_name}_${TIMESTAMP}.log"
    local dashboard_dir="${RESULTS_DIR}/${test_name}_${TIMESTAMP}_dashboard"
    
    echo "Running test plan: ${test_name}"
    echo "Results will be saved to: ${result_file}"
    echo "Log will be saved to: ${log_file}"
    
    # Run JMeter test in non-GUI mode
    ${JMETER_BIN} -n -t "${test_plan}" \
        -l "${result_file}" \
        -j "${log_file}" \
        -e -o "${dashboard_dir}"
    
    local exit_code=$?
    if [ ${exit_code} -eq 0 ]; then
        echo "Test completed successfully: ${test_name}"
        echo "Dashboard report generated at: ${dashboard_dir}"
    else
        echo "Test failed with exit code ${exit_code}: ${test_name}"
    fi
    
    echo "----------------------------------------"
}

# Function to run all test plans
run_all_test_plans() {
    echo "Starting performance tests at $(date)"
    echo "----------------------------------------"
    
    for test_plan in "${TEST_PLANS_DIR}"/*.jmx; do
        run_test_plan "${test_plan}"
    done
    
    echo "All performance tests completed at $(date)"
    echo "Results are available in ${RESULTS_DIR}"
}

# Function to run a specific test plan
run_specific_test_plan() {
    local test_name="$1"
    local test_plan="${TEST_PLANS_DIR}/${test_name}.jmx"
    
    if [ ! -f "${test_plan}" ]; then
        echo "Error: Test plan not found: ${test_plan}"
        exit 1
    fi
    
    run_test_plan "${test_plan}"
}

# Function to analyze results
analyze_results() {
    echo "Analyzing performance test results"
    echo "----------------------------------------"
    
    # Find the most recent result files
    local latest_results=$(find "${RESULTS_DIR}" -name "*.jtl" -type f -printf '%T@ %p\n' | sort -n | tail -3 | cut -f2- -d" ")
    
    for result in ${latest_results}; do
        local test_name=$(basename "${result}" | sed 's/_[0-9]*_[0-9]*.jtl//')
        echo "Analyzing results for: ${test_name}"
        
        # Extract key metrics using awk
        echo "Summary statistics:"
        awk -F, 'NR>1 {sum+=$1; count++; if($1>max) max=$1; if(min==0 || $1<min) min=$1} 
            END {
                print "  Samples: " count;
                print "  Average response time: " (count>0 ? sum/count : 0) " ms";
                print "  Min response time: " min " ms";
                print "  Max response time: " max " ms";
            }' "${result}"
        
        # Count errors
        local errors=$(grep -c ",false," "${result}")
        echo "  Errors: ${errors}"
        
        echo "----------------------------------------"
    done
}

# Main execution
case "$1" in
    "all")
        run_all_test_plans
        ;;
    "analyze")
        analyze_results
        ;;
    "")
        echo "Usage: $0 [all|analyze|<test_name>]"
        echo "  all        - Run all test plans"
        echo "  analyze    - Analyze recent test results"
        echo "  <test_name> - Run a specific test plan (without .jmx extension)"
        echo ""
        echo "Available test plans:"
        for test_plan in "${TEST_PLANS_DIR}"/*.jmx; do
            echo "  $(basename "${test_plan}" .jmx)"
        done
        exit 1
        ;;
    *)
        run_specific_test_plan "$1"
        ;;
esac

exit 0