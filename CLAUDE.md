# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- **Build the mod**: `./gradlew build`
- **Clean build**: `./gradlew clean build`
- **Run in development**: `./gradlew runClient`
- **Generate sources**: `./gradlew genSources`

The built JAR file will be located in `build/libs/skyblock-item-compare-0.0.1.jar`.

## Project Architecture

This is a Fabric mod for Minecraft 1.21.5 that allows comparing Hypixel Skyblock item stats side-by-side.

### Core Components

- **SkyblockItemCompare** (main): Main mod initializer, sets up logging and mod ID
- **SkyblockItemCompareClient** (client): Client-side initialization, registers keybindings:
  - M key: Select/compare items
  - R key: Reset selection
  - Handles key events both in inventory screens and globally
- **ItemComparator**: Core comparison logic that manages the two-item selection workflow:
  - First item selection stores the item
  - Second item selection triggers comparison screen
  - Uses reflection to access private HandledScreen methods for mouse position detection
- **ItemComparisonScreen**: GUI screen that displays two items side-by-side with their parsed stats
- **SkyblockItemStats**: Parses Hypixel Skyblock item tooltips using regex patterns to extract:
  - Damage, Defense, Strength, Crit Chance, Crit Damage, Speed, Health, Mana
  - Item rarity and type
  - Formats stats with appropriate colors

### Key Technical Details

- Uses Fabric Loom for mod development
- Requires Java 21 and Minecraft 1.21.5
- Uses reflection to access private Minecraft methods for slot detection
- Mixin system configured but HandledScreenMixin is not currently registered in mixins.json
- Localization support via `en_us.json` with translatable text keys

### Dependencies

- Fabric Loader 0.16.14+
- Fabric API 0.128.1+1.21.5
- Minecraft 1.21.5
- Yarn mappings for deobfuscation

### Development Notes

- The mod uses split environment source sets (main and client)
- Debug logging is enabled throughout for development
- Item selection workflow: hover over item → press M → hover over second item → press M → comparison screen opens
- Reset selection with R key at any time