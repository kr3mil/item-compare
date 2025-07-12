#!/bin/bash
set -e

echo "🔨 Building all Minecraft mod platforms using Docker..."

# Build modern platforms (Fabric 1.21.5)
echo "📦 Building modern platforms..."
docker-compose run --rm modern-build

# Build legacy Forge 1.8.9  
echo "📦 Building legacy Forge 1.8.9..."
docker-compose run --rm legacy-build

echo "✅ All builds completed!"
echo ""
echo "📁 Build artifacts:"
echo "  - Fabric 1.21.5: fabric-1.21.5/build/libs/"
echo "  - Legacy Forge:   forge-1.8.9/build/libs/"

# List the actual built files
echo ""
echo "🎯 Built JAR files:"
find . -name "*.jar" -path "*/build/libs/*" -not -path "*/build/libs/*-sources.jar" -not -path "*/build/libs/*-dev.jar" | sort