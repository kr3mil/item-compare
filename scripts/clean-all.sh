#!/bin/bash
echo "🧹 Cleaning all build artifacts..."

# Clean using containers
docker-compose run --rm modern-build ./gradlew cleanAll --no-daemon
docker-compose run --rm legacy-build ./gradlew clean --no-daemon

# Clean Docker images and volumes
echo "🗑️  Cleaning Docker resources..."
docker-compose down -v
docker system prune -f

echo "✅ All clean!"