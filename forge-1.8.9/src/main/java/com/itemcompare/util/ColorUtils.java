package com.itemcompare.util;

public class ColorUtils {

  /**
   * Returns the color code for the given rarity.
   *
   * @param rarity The rarity string (e.g., "COMMON", "LEGENDARY")
   * @return The Minecraft color code
   */
  public static String getRarityColor(String rarity) {
    switch (rarity.toUpperCase()) {
      case "COMMON":
        return "f";
      case "UNCOMMON":
        return "a";
      case "RARE":
        return "9";
      case "EPIC":
        return "5";
      case "LEGENDARY":
        return "6";
      case "MYTHIC":
        return "d";
      case "DIVINE":
        return "b";
      case "SPECIAL":
        return "c";
      default:
        return "f";
    }
  }

  /**
   * Returns the color code for the given stat name.
   *
   * @param stat The stat name (e.g., "Damage", "Health")
   * @return The Minecraft color code with § prefix
   */
  public static String getStatColor(String stat) {
    switch (stat) {
      case "Damage":
      case "Strength":
      case "Crit Chance":
      case "Crit Damage":
      case "Ferocity":
      case "Ability Damage":
      case "Bonus Attack Speed":
        return "§c";
      case "Defense":
      case "Health":
      case "Speed":
      case "True Defense":
      case "Vitality":
        return "§a";
      case "Mana":
      case "Intelligence":
        return "§b";
      case "Mining Speed":
      case "Mining Fortune":
      case "Breaking Power":
      case "Farming Fortune":
      case "Foraging Fortune":
        return "§6"; // Gold for mining/farming stats
      case "Fishing Speed":
      case "Sea Creature Chance":
        return "§3"; // Dark aqua for fishing stats
      case "Magic Find":
      case "Pet Luck":
        return "§d"; // Light purple for luck-based stats
      default:
        return "§a";
    }
  }
}