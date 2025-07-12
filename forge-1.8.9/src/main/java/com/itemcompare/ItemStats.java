package com.itemcompare;

import com.itemcompare.util.ItemStatsCommon;
import java.util.*;
import net.minecraft.item.ItemStack;

public class ItemStats extends ItemStatsCommon {

  public ItemStats(String itemName, String rarity, String itemType) {
    super(itemName, rarity, itemType);
  }

  public static ItemStats parseItem(ItemStack item) {
    String itemName = item.getDisplayName();

    ItemCompare.logger.info("=== PARSING ITEM: " + itemName + " ===");

    // Get tooltip lines for the item
    List<String> tooltipLines = new ArrayList<String>();
    try {
      // Use reflection to get tooltip from item
      // In 1.8.9, tooltip is usually on the item itself
      @SuppressWarnings("unchecked")
      List<String> lore = item.getTooltip(null, false);
      
      if (lore != null) {
        tooltipLines.addAll(lore);
      }
      
      ItemCompare.logger.info("Total lore lines: " + tooltipLines.size());

      // Convert tooltip lines to plain strings (remove formatting codes)
      List<String> plainTooltipLines = new ArrayList<String>();
      for (int i = 0; i < tooltipLines.size(); i++) {
        String line = tooltipLines.get(i);
        // Remove Minecraft formatting codes
        String plainLine = line.replaceAll("§[0-9a-fk-or]", "");
        plainTooltipLines.add(plainLine);
        ItemCompare.logger.info("Line " + i + ": '" + plainLine + "'");
      }

      // Use common parsing logic
      ItemStatsCommon commonStats = ItemStatsCommon.parseFromTooltip(itemName, plainTooltipLines);

      // Create Forge-specific wrapper
      ItemStats stats = new ItemStats(commonStats.getItemName(), commonStats.getRarity(), "UNKNOWN");
      stats.stats.putAll(commonStats.stats);
      stats.percentageStats.addAll(commonStats.percentageStats);
      stats.enchantments.putAll(commonStats.enchantments);

      ItemCompare.logger.info("Final parsed stats: " + stats.stats);
      ItemCompare.logger.info("=== END PARSING ===");

      return stats;
    } catch (Exception e) {
      ItemCompare.logger.error("Error parsing item tooltip: " + e.getMessage());
      // Return empty stats object
      return new ItemStats(itemName, "COMMON", "UNKNOWN");
    }
  }
}