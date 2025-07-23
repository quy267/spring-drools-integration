#!/bin/bash

# Deployment script for Spring Boot Drools Integration
# This script handles Docker Compose deployment for different environments

set -e

# Configuration
APP_NAME="spring-drools-integration"
COMPOSE_FILE="docker-compose.yml"
ENV=${1:-"development"}
ACTION=${2:-"up"}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check if Docker is running
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    
    # Check if Docker Compose is available
    if ! command -v docker-compose >/dev/null 2>&1 && ! docker compose version >/dev/null 2>&1; then
        log_error "Docker Compose is not available. Please install Docker Compose."
        exit 1
    fi
    
    # Check if compose file exists
    if [ ! -f "$COMPOSE_FILE" ]; then
        log_error "Docker Compose file not found: $COMPOSE_FILE"
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

# Set environment variables based on deployment environment
set_environment() {
    log_info "Setting up environment: $ENV"
    
    case $ENV in
        "development"|"dev")
            export SPRING_PROFILES_ACTIVE="dev,docker"
            export LOG_LEVEL="DEBUG"
            export POSTGRES_DB="ruledb_dev"
            ;;
        "staging"|"stage")
            export SPRING_PROFILES_ACTIVE="staging,docker"
            export LOG_LEVEL="INFO"
            export POSTGRES_DB="ruledb_staging"
            ;;
        "production"|"prod")
            export SPRING_PROFILES_ACTIVE="prod,docker"
            export LOG_LEVEL="WARN"
            export POSTGRES_DB="ruledb_prod"
            ;;
        *)
            log_warning "Unknown environment: $ENV. Using development settings."
            export SPRING_PROFILES_ACTIVE="dev,docker"
            export LOG_LEVEL="DEBUG"
            export POSTGRES_DB="ruledb_dev"
            ;;
    esac
    
    log_success "Environment configured for: $ENV"
}

# Create necessary directories
create_directories() {
    log_info "Creating necessary directories..."
    
    mkdir -p logs
    mkdir -p monitoring/prometheus
    mkdir -p monitoring/grafana/provisioning
    mkdir -p monitoring/grafana/dashboards
    mkdir -p docker/postgres
    
    log_success "Directories created"
}

# Generate environment-specific configuration files
generate_configs() {
    log_info "Generating configuration files..."
    
    # Create PostgreSQL init script if it doesn't exist
    if [ ! -f "docker/postgres/init.sql" ]; then
        mkdir -p docker/postgres
        cat > docker/postgres/init.sql << EOF
-- Initialize database for Spring Boot Drools Integration
CREATE DATABASE IF NOT EXISTS ${POSTGRES_DB};
GRANT ALL PRIVILEGES ON DATABASE ${POSTGRES_DB} TO ruleuser;

-- Create tables for rule management (if needed)
\c ${POSTGRES_DB};

CREATE TABLE IF NOT EXISTS rule_execution_log (
    id SERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    execution_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    input_data TEXT,
    output_data TEXT,
    execution_duration_ms BIGINT,
    status VARCHAR(50) DEFAULT 'SUCCESS'
);

CREATE INDEX IF NOT EXISTS idx_rule_execution_time ON rule_execution_log(execution_time);
CREATE INDEX IF NOT EXISTS idx_rule_name ON rule_execution_log(rule_name);
EOF
        log_success "PostgreSQL init script created"
    fi
    
    # Create Prometheus configuration if it doesn't exist
    if [ ! -f "monitoring/prometheus/prometheus.yml" ]; then
        mkdir -p monitoring/prometheus
        cat > monitoring/prometheus/prometheus.yml << EOF
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "rules/*.yml"

scrape_configs:
  - job_name: 'spring-drools-app'
    static_configs:
      - targets: ['spring-drools-app:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
EOF
        log_success "Prometheus configuration created"
    fi
}

# Deploy using Docker Compose
deploy() {
    log_info "Starting deployment with action: $ACTION"
    
    case $ACTION in
        "up"|"start")
            log_info "Starting services..."
            docker-compose up -d
            wait_for_services
            show_service_status
            ;;
        "down"|"stop")
            log_info "Stopping services..."
            docker-compose down
            ;;
        "restart")
            log_info "Restarting services..."
            docker-compose restart
            wait_for_services
            show_service_status
            ;;
        "logs")
            docker-compose logs -f
            ;;
        "ps"|"status")
            show_service_status
            ;;
        *)
            log_error "Unknown action: $ACTION"
            show_help
            exit 1
            ;;
    esac
}

# Wait for services to be healthy
wait_for_services() {
    log_info "Waiting for services to be healthy..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker-compose ps | grep -q "Up (healthy)"; then
            log_success "Services are healthy"
            return 0
        fi
        
        log_info "Attempt $attempt/$max_attempts - Waiting for services..."
        sleep 10
        attempt=$((attempt + 1))
    done
    
    log_warning "Services may not be fully healthy yet. Check logs for details."
}

# Show service status
show_service_status() {
    log_info "Service status:"
    docker-compose ps
    
    log_info "Application health check:"
    if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
        log_success "Application is healthy"
    else
        log_warning "Application health check failed"
    fi
    
    log_info "Access URLs:"
    echo "  Application: http://localhost:8080"
    echo "  Health Check: http://localhost:8080/actuator/health"
    echo "  Metrics: http://localhost:8080/actuator/metrics"
    echo "  Prometheus: http://localhost:9090"
    echo "  Grafana: http://localhost:3000 (admin/admin123)"
}

# Cleanup resources
cleanup() {
    log_info "Cleaning up resources..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    log_success "Cleanup completed"
}

# Show help
show_help() {
    echo "Usage: $0 [ENVIRONMENT] [ACTION]"
    echo ""
    echo "Deploy Spring Boot Drools Integration using Docker Compose"
    echo ""
    echo "Arguments:"
    echo "  ENVIRONMENT   Deployment environment (development|staging|production)"
    echo "                Default: development"
    echo "  ACTION        Deployment action (up|down|restart|logs|status|cleanup)"
    echo "                Default: up"
    echo ""
    echo "Examples:"
    echo "  $0                          # Deploy to development"
    echo "  $0 production up            # Deploy to production"
    echo "  $0 staging restart          # Restart staging environment"
    echo "  $0 development logs         # Show logs for development"
    echo "  $0 production status        # Show status for production"
    echo "  $0 development cleanup      # Cleanup development resources"
}

# Main execution
main() {
    log_info "Starting deployment process"
    log_info "Environment: $ENV"
    log_info "Action: $ACTION"
    
    if [ "$ACTION" == "cleanup" ]; then
        cleanup
        return 0
    fi
    
    check_prerequisites
    set_environment
    create_directories
    generate_configs
    deploy
    
    log_success "Deployment process completed!"
}

# Check for help flag
if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
    show_help
    exit 0
fi

# Run main function
main