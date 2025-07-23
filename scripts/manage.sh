#!/bin/bash

# Container management utility script for Spring Boot Drools Integration
# This script provides common container management operations

set -e

# Configuration
APP_NAME="spring-drools-integration"
CONTAINER_NAME="spring-drools-integration"

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

# Check if container exists
container_exists() {
    docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"
}

# Check if container is running
container_running() {
    docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"
}

# Show container logs
show_logs() {
    local lines=${1:-100}
    local follow=${2:-false}
    
    if ! container_exists; then
        log_error "Container $CONTAINER_NAME does not exist"
        return 1
    fi
    
    log_info "Showing logs for $CONTAINER_NAME (last $lines lines)"
    
    if [ "$follow" = "true" ]; then
        docker logs -f --tail "$lines" "$CONTAINER_NAME"
    else
        docker logs --tail "$lines" "$CONTAINER_NAME"
    fi
}

# Execute command in container
exec_command() {
    local cmd="$1"
    
    if ! container_running; then
        log_error "Container $CONTAINER_NAME is not running"
        return 1
    fi
    
    log_info "Executing command in $CONTAINER_NAME: $cmd"
    docker exec -it "$CONTAINER_NAME" $cmd
}

# Get container shell
get_shell() {
    if ! container_running; then
        log_error "Container $CONTAINER_NAME is not running"
        return 1
    fi
    
    log_info "Opening shell in $CONTAINER_NAME"
    docker exec -it "$CONTAINER_NAME" /bin/bash
}

# Show container stats
show_stats() {
    if ! container_running; then
        log_error "Container $CONTAINER_NAME is not running"
        return 1
    fi
    
    log_info "Container statistics for $CONTAINER_NAME:"
    docker stats --no-stream "$CONTAINER_NAME"
}

# Show container info
show_info() {
    if ! container_exists; then
        log_error "Container $CONTAINER_NAME does not exist"
        return 1
    fi
    
    log_info "Container information for $CONTAINER_NAME:"
    docker inspect "$CONTAINER_NAME" --format='
Image: {{.Config.Image}}
Status: {{.State.Status}}
Started: {{.State.StartedAt}}
Ports: {{range $p, $conf := .NetworkSettings.Ports}}{{$p}} -> {{(index $conf 0).HostPort}} {{end}}
Environment: {{range .Config.Env}}
  {{.}}{{end}}
Mounts: {{range .Mounts}}
  {{.Source}} -> {{.Destination}} ({{.Mode}}){{end}}
'
}

# Health check
health_check() {
    if ! container_running; then
        log_error "Container $CONTAINER_NAME is not running"
        return 1
    fi
    
    log_info "Performing health check..."
    
    # Check container health status
    local health_status=$(docker inspect "$CONTAINER_NAME" --format='{{.State.Health.Status}}' 2>/dev/null || echo "no-healthcheck")
    
    if [ "$health_status" = "healthy" ]; then
        log_success "Container health check: HEALTHY"
    elif [ "$health_status" = "unhealthy" ]; then
        log_error "Container health check: UNHEALTHY"
        return 1
    else
        log_warning "Container health check: $health_status"
    fi
    
    # Check application endpoints
    local app_port=$(docker port "$CONTAINER_NAME" 8080 2>/dev/null | cut -d: -f2)
    if [ -n "$app_port" ]; then
        if curl -f "http://localhost:$app_port/actuator/health" >/dev/null 2>&1; then
            log_success "Application health endpoint: HEALTHY"
        else
            log_error "Application health endpoint: UNHEALTHY"
            return 1
        fi
    else
        log_warning "Application port not exposed"
    fi
}

# Backup container data
backup_data() {
    local backup_dir="./backups/$(date +%Y%m%d_%H%M%S)"
    
    if ! container_exists; then
        log_error "Container $CONTAINER_NAME does not exist"
        return 1
    fi
    
    log_info "Creating backup in $backup_dir"
    mkdir -p "$backup_dir"
    
    # Backup application logs
    if docker exec "$CONTAINER_NAME" test -d /app/logs 2>/dev/null; then
        docker cp "$CONTAINER_NAME:/app/logs" "$backup_dir/"
        log_success "Logs backed up"
    fi
    
    # Backup uploaded rules
    if docker exec "$CONTAINER_NAME" test -d /app/uploads 2>/dev/null; then
        docker cp "$CONTAINER_NAME:/app/uploads" "$backup_dir/"
        log_success "Uploads backed up"
    fi
    
    # Backup rule backups
    if docker exec "$CONTAINER_NAME" test -d /app/backups 2>/dev/null; then
        docker cp "$CONTAINER_NAME:/app/backups" "$backup_dir/rule_backups"
        log_success "Rule backups backed up"
    fi
    
    log_success "Backup completed: $backup_dir"
}

# Restore container data
restore_data() {
    local backup_dir="$1"
    
    if [ -z "$backup_dir" ] || [ ! -d "$backup_dir" ]; then
        log_error "Backup directory not specified or does not exist"
        return 1
    fi
    
    if ! container_running; then
        log_error "Container $CONTAINER_NAME is not running"
        return 1
    fi
    
    log_info "Restoring data from $backup_dir"
    
    # Restore logs
    if [ -d "$backup_dir/logs" ]; then
        docker cp "$backup_dir/logs" "$CONTAINER_NAME:/app/"
        log_success "Logs restored"
    fi
    
    # Restore uploads
    if [ -d "$backup_dir/uploads" ]; then
        docker cp "$backup_dir/uploads" "$CONTAINER_NAME:/app/"
        log_success "Uploads restored"
    fi
    
    # Restore rule backups
    if [ -d "$backup_dir/rule_backups" ]; then
        docker cp "$backup_dir/rule_backups" "$CONTAINER_NAME:/app/backups"
        log_success "Rule backups restored"
    fi
    
    log_success "Data restoration completed"
}

# Show help
show_help() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Container management utility for Spring Boot Drools Integration"
    echo ""
    echo "Commands:"
    echo "  logs [lines] [follow]   Show container logs (default: 100 lines)"
    echo "  exec <command>          Execute command in container"
    echo "  shell                   Open shell in container"
    echo "  stats                   Show container statistics"
    echo "  info                    Show container information"
    echo "  health                  Perform health check"
    echo "  backup                  Backup container data"
    echo "  restore <backup_dir>    Restore data from backup"
    echo "  help                    Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 logs                 # Show last 100 log lines"
    echo "  $0 logs 500 true        # Show last 500 lines and follow"
    echo "  $0 exec 'ps aux'        # Execute ps command in container"
    echo "  $0 shell                # Open interactive shell"
    echo "  $0 health               # Check application health"
    echo "  $0 backup               # Create backup of container data"
    echo "  $0 restore ./backups/20231201_120000  # Restore from backup"
}

# Main execution
main() {
    local command="$1"
    shift || true
    
    case "$command" in
        "logs")
            show_logs "$1" "$2"
            ;;
        "exec")
            if [ -z "$1" ]; then
                log_error "Command required for exec"
                exit 1
            fi
            exec_command "$*"
            ;;
        "shell")
            get_shell
            ;;
        "stats")
            show_stats
            ;;
        "info")
            show_info
            ;;
        "health")
            health_check
            ;;
        "backup")
            backup_data
            ;;
        "restore")
            if [ -z "$1" ]; then
                log_error "Backup directory required for restore"
                exit 1
            fi
            restore_data "$1"
            ;;
        "help"|"--help"|"-h"|"")
            show_help
            ;;
        *)
            log_error "Unknown command: $command"
            show_help
            exit 1
            ;;
    esac
}

# Run main function
main "$@"