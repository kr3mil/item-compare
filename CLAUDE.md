# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Multi-Platform Project Structure

This project supports multiple Minecraft versions and mod loaders:

- **fabric-1.21.5/**: Fabric mod for Minecraft 1.21.5
- **forge-1.21.5/**: NeoForge mod for Minecraft 1.21.4+ (work in progress)  
- **forge-1.8.9/**: Legacy Forge mod for Minecraft 1.8.9 (requires separate build)

Each platform version contains its own copy of the shared code in a `util/` package to avoid dependency complexities.

## Build Commands

### 🐳 Containerized Builds (Recommended)
The easiest way to build all platforms without version conflicts:

- **Build all platforms**: `./build-all.sh`
- **Clean all builds**: `./scripts/clean-all.sh`
- **Fabric development**: `./scripts/dev-fabric.sh`

Individual containerized builds:
- **Modern platforms**: `docker-compose run --rm modern-build`
- **Legacy Forge 1.8.9**: `docker-compose run --rm legacy-build`

### 🔧 Native Build Commands  
For local development without Docker:

#### Universal Build Commands
- **Build modern platforms**: `./gradlew buildAll`
- **Clean modern platforms**: `./gradlew cleanAll`

#### Platform-Specific Build Commands
- **Build Fabric 1.21.5**: `./gradlew :fabric-1.21.5:build`
- **Build modern Forge**: `./gradlew :forge-1.21.5:build` (currently needs version fixes)

#### Legacy Forge 1.8.9 (Java 8 required)
- **With Java version check**: `./scripts/build-legacy-separate.sh`
- **Direct build**: `cd forge-1.8.9 && ./gradlew build`

#### Development Commands
- **Run Fabric client**: `./gradlew runFabric`
- **Java version management**: `./scripts/setup-java-versions.sh`

## Code Quality Commands

### Fabric Project
- **Format code**: `./gradlew :fabric-1.21.5:spotlessApply`
- **Check formatting**: `./gradlew :fabric-1.21.5:spotlessCheck`
- **Run linting**: `./gradlew :fabric-1.21.5:checkstyleMain :fabric-1.21.5:checkstyleClient`

The built JAR files will be located in:
- Fabric: `fabric-1.21.5/build/libs/item-compare-0.0.2.jar`
- Modern Forge: `forge-1.21.5/build/libs/item-compare-forge-0.0.2.jar`
- Legacy Forge: `forge-1.8.9/build/libs/item-compare-forge-1.8.9-0.0.2.jar`

## Project Architecture

This is a multi-platform mod for Minecraft that allows comparing Hypixel Skyblock item stats side-by-side.

### Core Components

- **Shared Utilities** (copied in each platform's `util/` package):
  - **ItemStatsCommon**: Core stat parsing using regex patterns
  - **ColorUtils**: Minecraft color code utilities for rarity and stat formatting
- **Platform-Specific Modules**: Minecraft version and mod loader specific implementations
  - **ItemCompare**: Main mod initializer for each platform
  - **ItemStats**: Platform-specific wrapper around ItemStatsCommon
  - **ItemComparator**: Platform-specific UI interaction handling
  - **ItemComparisonScreen**: GUI screen for side-by-side comparison

### Key Features

- Parses Hypixel Skyblock item tooltips to extract:
  - Damage, Defense, Strength, Crit Chance, Crit Damage, Speed, Health, Mana
  - Mining stats, Fishing stats, Magic Find, and more
  - Item rarity and enchantments
- Side-by-side comparison screen with color-coded differences
- Keybinding support (M key for item selection)

### Technical Details

- **Shared code**: Java 8 compatible, copied to each platform's `util/` package
- **Fabric**: Uses Fabric Loom, Java 21, Minecraft 1.21.5
- **Modern Forge**: Uses NeoForge ModDev, Java 21, Minecraft 1.21.4+
- **Legacy Forge**: Uses ForgeGradle 2.1, Java 8, Gradle 4.9, Minecraft 1.8.9
- Uses reflection for private method access where needed
- Regex-based tooltip parsing for robust stat extraction
- No cross-platform dependencies - each version is self-contained

### Development Notes

- The mod uses split environment source sets (main and client) for Fabric
- Debug logging is enabled throughout for development
- Item selection workflow: hover over item → press M → hover over second item → press M → comparison screen opens
- Legacy Forge 1.8.9 requires separate Gradle wrapper due to compatibility constraints