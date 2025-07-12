#!/bin/bash
# Script to help manage multiple Java versions for different builds
echo "🔧 Java Version Management for Multi-Platform Builds"
echo ""

# Check current Java version
echo "Current Java version:"
java -version 2>&1 | head -1

echo ""
echo "📋 Recommended setup for this project:"
echo ""
echo "For modern platforms (Fabric 1.21.5, NeoForge):"
echo "  - Java 21 (OpenJDK 21 LTS)"
echo "  - Current Gradle (8.14+)"
echo ""
echo "For legacy Forge 1.8.9:"
echo "  - Java 8 (OpenJDK 8 LTS)" 
echo "  - Gradle 2.14 (included in project)"
echo ""

# Detect if SDKMAN is available
if command -v sdk &> /dev/null; then
    echo "✅ SDKMAN detected! You can manage Java versions with:"
    echo "  sdk install java 21.0.2-open    # For modern builds"
    echo "  sdk install java 8.0.392-open   # For legacy builds"
    echo "  sdk use java 21.0.2-open        # Switch to Java 21"
    echo "  sdk use java 8.0.392-open       # Switch to Java 8"
else
    echo "💡 Consider installing SDKMAN for easy Java version management:"
    echo "  curl -s 'https://get.sdkman.io' | bash"
    echo "  source ~/.sdkman/bin/sdkman-init.sh"
fi

echo ""
echo "🚀 Alternative: Use the containerized builds instead!"
echo "  ./build-all.sh                    # Build all platforms with Docker"
echo "  ./scripts/dev-fabric.sh           # Run Fabric dev environment"