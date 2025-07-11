# Item Compare

An item comparison tool that allows you to compare item stats and enchantments side-by-side with visual indicators.

**[📦 Download on Modrinth](https://modrinth.com/mod/skyblock-item-compare)**

## 🎮 Features

- **Clean comparison interface** - Side-by-side item comparison with intuitive UI
- **Automatic stat detection** - Dynamically parses ANY stat pattern (Mining Speed, Mining Fortune, Breaking Power, etc.)
- **Enchantment comparison** - Shows enchantment differences with Roman numerals (I, II, III, etc.)
- **Visual indicators** - Green/red +/- indicators for stat improvements and downgrades
- **Generic parsing system** - Future-proof design that automatically supports new stats
- **Hypixel Skyblock optimized** - Full support for all Hypixel Skyblock stat patterns and items
- **No configuration needed** - Works out of the box with any items

## 🛠️ Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.16.14+ for Minecraft 1.21.5
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the mod JAR from [releases](https://github.com/kr3mil/item-compare/releases)
4. Place the JAR file in your `mods` folder

## 🕹️ How to Use

1. **Select first item**: Hover over any item in your inventory and press `M`
2. **Select second item**: Hover over another item and press `M` again
3. **View comparison**: The comparison screen opens automatically
4. **Close comparison**: Press `ESC` or click "Close" to exit

## 📊 What Gets Compared

### Stats (Automatically Detected)
- **Combat**: Damage, Defense, Strength, Crit Chance, Crit Damage, Health
- **Mining**: Mining Speed, Mining Fortune, Breaking Power  
- **Other**: Speed, Mana, Intelligence, Ferocity, and ANY other stat pattern!

### Enchantments
- All enchantments with their levels (I, II, III, IV, V, etc.)
- Missing enchantments shown as "None"
- Level differences with +/- indicators

## 🎯 Compatibility

- **Minecraft**: 1.21.5
- **Mod Loader**: Fabric  
- **Dependencies**: Fabric API
- **Environment**: Client-side only

## 🔧 Building

To build the mod yourself:

```bash
./gradlew build
```

The built JAR will be in `build/libs/`

## 🛠️ Development

This mod is built with:
- Minecraft 1.21.5
- Fabric Loader 0.16.14+
- Fabric API 0.128.1+1.21.5
- Java 21

## 🐛 Issues & Support

Found a bug or have a feature request? Please report it on the [GitHub Issues](https://github.com/kr3mil/item-compare/issues) page.

## ☕ Support

If you enjoy this mod and want to support development, consider buying me a coffee:

[![Support](https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-orange)](https://coff.ee/kr3mil)

## License

MIT License