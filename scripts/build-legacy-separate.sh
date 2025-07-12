#!/bin/bash
set -e

echo "🏗️  Building legacy Forge 1.8.9 (requires Java 8)..."
echo ""

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1-2)
if [[ "$JAVA_VERSION" != "1.8" ]]; then
    echo "⚠️  Warning: Legacy Forge 1.8.9 requires Java 8"
    echo "   Current Java version: $JAVA_VERSION"
    echo ""
    echo "💡 Options:"
    echo "   1. Switch to Java 8 and run this script again"
    echo "   2. Use Docker: docker-compose run --rm legacy-build"
    echo "   3. Install SDKMAN and switch: sdk use java 8.0.392-open"
    echo ""
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Build the legacy project
cd forge-1.8.9

echo "📦 Building with Gradle 2.14..."
./gradlew build --no-daemon

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Legacy Forge 1.8.9 build completed!"
    echo "📁 JAR file: forge-1.8.9/build/libs/"
    find build/libs/ -name "*.jar" -not -name "*-sources.jar" | head -1
else
    echo "❌ Build failed!"
    exit 1
fi