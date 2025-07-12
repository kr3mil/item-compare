package com.itemcompare;

import com.itemcompare.util.ColorUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemStats {
  public final Map<String, Integer> stats = new HashMap<>();
  public final Set<String> percentageStats = new HashSet<>();
  public final Map<String, Integer> enchantments = new HashMap<>();
  private final String itemName;
  private String rarity;
  private final String itemType;

  // Generic patterns to catch all stats
  private static final Pattern STAT_WITH_PERCENT_PATTERN =
      Pattern.compile("([A-Za-z][A-Za-z ]+): \\+?(\\d+)%");
  private static final Pattern STAT_WITH_VALUE_PATTERN =
      Pattern.compile("([A-Za-z][A-Za-z ]+): \\+?(\\d+)(?!%)");
  private static final Pattern BREAKING_POWER_PATTERN = Pattern.compile("Breaking Power (\\d+)");
  private static final Pattern RARITY_PATTERN =
      Pattern.compile("([A-Z]+) [A-Z]+$"); // UNCOMMON PICKAXE, RARE SWORD, etc.
  private static final Pattern ENCHANTMENT_PATTERN =
      Pattern.compile("([A-Za-z][A-Za-z ]+) ([IVX]+)"); // Enchantment Name I, II, III, IV, V, etc.

  public ItemStats(String itemName, String rarity, String itemType) {
    this.itemName = itemName;
    this.rarity = rarity;
    this.itemType = itemType;
  }

  public static ItemStats parseItem(ItemStack item) {
    String itemName = item.getName().getString();
    String rarity = "COMMON";
    String itemType = "UNKNOWN";

    ItemCompare.LOGGER.info("=== PARSING ITEM: " + itemName + " ===");

    ItemStats stats = new ItemStats(itemName, rarity, itemType);

    List<Text> lore =
        item.getTooltip(
            net.minecraft.item.Item.TooltipContext.DEFAULT,
            null,
            net.minecraft.item.tooltip.TooltipType.BASIC);

    ItemCompare.LOGGER.info("Total lore lines: " + lore.size());

    for (int i = 0; i < lore.size(); i++) {
      Text line = lore.get(i);
      String lineText = line.getString();

      ItemCompare.LOGGER.info("Line " + i + ": '" + lineText + "'");

      // Parse rarity
      Matcher rarityMatcher = RARITY_PATTERN.matcher(lineText);
      if (rarityMatcher.find()) {
        rarity = rarityMatcher.group(1);
        ItemCompare.LOGGER.info("Found rarity: " + rarity);
      }

      // Parse percentage stats first
      Matcher percentMatcher = STAT_WITH_PERCENT_PATTERN.matcher(lineText);
      if (percentMatcher.find()) {
        String statName = percentMatcher.group(1).trim();
        int value = Integer.parseInt(percentMatcher.group(2));
        if (!stats.stats.containsKey(statName)) {
          stats.stats.put(statName, value);
          stats.percentageStats.add(statName);
          ItemCompare.LOGGER.info("Percentage stat found: " + statName + " = " + value + "%");
        }
      }

      // Parse regular stats
      Matcher valueMatcher = STAT_WITH_VALUE_PATTERN.matcher(lineText);
      if (valueMatcher.find()) {
        String statName = valueMatcher.group(1).trim();
        int value = Integer.parseInt(valueMatcher.group(2));
        if (!stats.stats.containsKey(statName)) {
          stats.stats.put(statName, value);
          ItemCompare.LOGGER.info("Value stat found: " + statName + " = " + value);
        }
      }

      // Parse special case: Breaking Power
      Matcher breakingPowerMatcher = BREAKING_POWER_PATTERN.matcher(lineText);
      if (breakingPowerMatcher.find()) {
        String statName = "Breaking Power";
        int value = Integer.parseInt(breakingPowerMatcher.group(1));
        if (!stats.stats.containsKey(statName)) {
          stats.stats.put(statName, value);
          ItemCompare.LOGGER.info("Breaking Power found: " + statName + " = " + value);
        }
      }

      // Parse enchantments
      Matcher enchantmentMatcher = ENCHANTMENT_PATTERN.matcher(lineText);
      if (enchantmentMatcher.find()) {
        String enchantName = enchantmentMatcher.group(1).trim();
        String romanLevel = enchantmentMatcher.group(2);
        int level = romanToInt(romanLevel);

        // Skip if this is actually a stat (contains colon)
        if (!lineText.contains(":") && !stats.enchantments.containsKey(enchantName)) {
          stats.enchantments.put(enchantName, level);
          ItemCompare.LOGGER.info(
              "Enchantment found: " + enchantName + " = " + level + " (" + romanLevel + ")");
        }
      }
    }

    ItemCompare.LOGGER.info("Final parsed stats: " + stats.stats);
    ItemCompare.LOGGER.info("=== END PARSING ===");

    // Update the rarity in the stats object
    stats.rarity = rarity;

    return stats;
  }

  public boolean isPercentageStat(String statName) {
    return percentageStats.contains(statName);
  }

  private static int romanToInt(String roman) {
    Map<Character, Integer> romanMap = new HashMap<>();
    romanMap.put('I', 1);
    romanMap.put('V', 5);
    romanMap.put('X', 10);
    romanMap.put('L', 50);
    romanMap.put('C', 100);
    romanMap.put('D', 500);
    romanMap.put('M', 1000);

    int result = 0;
    for (int i = 0; i < roman.length(); i++) {
      int current = romanMap.get(roman.charAt(i));
      if (i + 1 < roman.length() && current < romanMap.get(roman.charAt(i + 1))) {
        result -= current;
      } else {
        result += current;
      }
    }
    return result;
  }

  public List<String> getFormattedStats() {
    List<String> formatted = new ArrayList<>();

    formatted.add("§6" + itemName);
    formatted.add("§7Rarity: §" + ColorUtils.getRarityColor(rarity) + rarity);
    formatted.add("");

    String[] statOrder = {
      "Damage", "Defense", "Strength", "Crit Chance", "Crit Damage", "Speed", "Health", "Mana"
    };

    for (String stat : statOrder) {
      if (stats.containsKey(stat)) {
        String color = ColorUtils.getStatColor(stat);
        String suffix = (stat.equals("Crit Chance") || stat.equals("Crit Damage")) ? "%" : "";
        formatted.add("§7" + stat + ": " + color + "+" + stats.get(stat) + suffix);
      }
    }

    for (Map.Entry<String, Integer> entry : stats.entrySet()) {
      if (!Arrays.asList(statOrder).contains(entry.getKey())) {
        formatted.add("§7" + entry.getKey() + ": §a+" + entry.getValue());
      }
    }

    return formatted;
  }

  public int getStat(String statName) {
    return stats.getOrDefault(statName, 0);
  }

  public String getItemName() {
    return itemName;
  }

  public String getRarity() {
    return rarity;
  }
}
