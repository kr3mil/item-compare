# 🐳 Containerized Multi-Platform Builds

This project supports building for multiple Minecraft versions and mod loaders using Docker containers to avoid version conflicts.

## Why Containerization?

- **Forge 1.8.9**: Requires Java 8 + Gradle 2.14 + ForgeGradle 2.3
- **Modern platforms**: Require Java 21 + Gradle 8+ + modern tooling
- **Conflicts**: Can't have both environments in the same system

## Quick Start

### Prerequisites
- Docker Desktop with WSL2 integration enabled
- Docker Compose

### Build All Platforms
```bash
./build-all.sh
```

This will:
1. Build Fabric 1.21.5 (modern environment)
2. Build legacy Forge 1.8.9 (legacy environment)
3. List all generated JAR files

### Individual Builds
```bash
# Modern platforms only
docker-compose run --rm modern-build

# Legacy Forge 1.8.9 only  
docker-compose run --rm legacy-build
```

### Development Environment
```bash
# Run Fabric client in container
./scripts/dev-fabric.sh
```

## Container Environments

### Modern Container (`Dockerfile.modern`)
- **Base**: OpenJDK 21
- **Platforms**: Fabric 1.21.5, NeoForge 1.21.x
- **Gradle**: 8.14+ (project wrapper)
- **Memory**: 2GB default

### Legacy Container (`Dockerfile.legacy`)
- **Base**: OpenJDK 8  
- **Platform**: Forge 1.8.9
- **Gradle**: 2.14 (project wrapper)
- **Memory**: 1GB default

## Files Structure

```
docker/
├── Dockerfile.modern          # Modern platforms container
└── Dockerfile.legacy          # Legacy platform container

scripts/
├── dev-fabric.sh             # Development environment
├── clean-all.sh              # Clean all builds
├── build-legacy-separate.sh   # Alternative legacy build
└── setup-java-versions.sh    # Java version management help

docker-compose.yml             # Orchestration config
build-all.sh                  # Main build script
.dockerignore                 # Docker ignore patterns
```

## Volume Mounts

- **Source code**: Live-mounted for development
- **Gradle caches**: Persistent volumes for faster builds
- **Minecraft run**: Persistent for development

## Alternative: Native Builds

If Docker isn't available, see the native build instructions in CLAUDE.md. You'll need to manage Java versions manually using tools like SDKMAN.

## Troubleshooting

### Docker not found in WSL2
Enable WSL2 integration in Docker Desktop settings.

### Build failures
Check container logs:
```bash
docker-compose logs modern-build
docker-compose logs legacy-build
```

### Clean everything
```bash
./scripts/clean-all.sh
```