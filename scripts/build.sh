#!/bin/bash

# Build script for Spring Boot Drools Integration
# This script builds the Docker image with proper tagging and optimization

set -e

# Configuration
APP_NAME="spring-drools-integration"
REGISTRY="localhost:5000"  # Change to your registry
VERSION=${1:-"latest"}
BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ')
GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")

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

# Check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Clean up old images
cleanup_old_images() {
    log_info "Cleaning up old images..."
    docker image prune -f --filter "label=app=${APP_NAME}" || true
}

# Build the Docker image
build_image() {
    log_info "Building Docker image: ${APP_NAME}:${VERSION}"
    
    docker build \
        --build-arg BUILD_DATE="${BUILD_DATE}" \
        --build-arg GIT_COMMIT="${GIT_COMMIT}" \
        --build-arg VERSION="${VERSION}" \
        --label "app=${APP_NAME}" \
        --label "version=${VERSION}" \
        --label "build-date=${BUILD_DATE}" \
        --label "git-commit=${GIT_COMMIT}" \
        --tag "${APP_NAME}:${VERSION}" \
        --tag "${APP_NAME}:latest" \
        .
    
    log_success "Docker image built successfully"
}

# Tag image for registry
tag_for_registry() {
    if [ "$REGISTRY" != "localhost:5000" ]; then
        log_info "Tagging image for registry: ${REGISTRY}"
        docker tag "${APP_NAME}:${VERSION}" "${REGISTRY}/${APP_NAME}:${VERSION}"
        docker tag "${APP_NAME}:${VERSION}" "${REGISTRY}/${APP_NAME}:latest"
        log_success "Image tagged for registry"
    fi
}

# Push to registry (optional)
push_to_registry() {
    if [ "$2" == "--push" ] && [ "$REGISTRY" != "localhost:5000" ]; then
        log_info "Pushing image to registry: ${REGISTRY}"
        docker push "${REGISTRY}/${APP_NAME}:${VERSION}"
        docker push "${REGISTRY}/${APP_NAME}:latest"
        log_success "Image pushed to registry"
    fi
}

# Display image information
show_image_info() {
    log_info "Image information:"
    docker images "${APP_NAME}" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
    
    log_info "Image labels:"
    docker inspect "${APP_NAME}:${VERSION}" --format='{{range $k, $v := .Config.Labels}}{{$k}}: {{$v}}{{"\n"}}{{end}}'
}

# Main execution
main() {
    log_info "Starting build process for ${APP_NAME}"
    log_info "Version: ${VERSION}"
    log_info "Build Date: ${BUILD_DATE}"
    log_info "Git Commit: ${GIT_COMMIT}"
    
    check_docker
    cleanup_old_images
    build_image
    tag_for_registry
    push_to_registry "$@"
    show_image_info
    
    log_success "Build process completed successfully!"
    log_info "To run the container: docker run -p 8080:8080 ${APP_NAME}:${VERSION}"
}

# Help function
show_help() {
    echo "Usage: $0 [VERSION] [OPTIONS]"
    echo ""
    echo "Build Docker image for Spring Boot Drools Integration"
    echo ""
    echo "Arguments:"
    echo "  VERSION     Image version tag (default: latest)"
    echo ""
    echo "Options:"
    echo "  --push      Push image to registry after building"
    echo "  --help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                    # Build with 'latest' tag"
    echo "  $0 1.0.0              # Build with '1.0.0' tag"
    echo "  $0 1.0.0 --push       # Build and push to registry"
}

# Check for help flag
if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
    show_help
    exit 0
fi

# Run main function
main "$@"