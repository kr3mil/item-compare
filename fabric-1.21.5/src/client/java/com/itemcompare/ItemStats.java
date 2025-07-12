package com.itemcompare;

import com.itemcompare.util.ItemStatsCommon;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemStats extends ItemStatsCommon {

  public ItemStats(String itemName, String rarity, String itemType) {
    super(itemName, rarity, itemType);
  }

  public static ItemStats parseItem(ItemStack item) {
    String itemName = item.getName().getString();

    ItemCompare.LOGGER.info("=== PARSING ITEM: " + itemName + " ===");

    List<Text> lore =
        item.getTooltip(
            net.minecraft.item.Item.TooltipContext.DEFAULT,
            null,
            net.minecraft.item.tooltip.TooltipType.BASIC);

    ItemCompare.LOGGER.info("Total lore lines: " + lore.size());

    // Convert Minecraft Text objects to plain strings
    List<String> tooltipLines = new ArrayList<>();
    for (int i = 0; i < lore.size(); i++) {
      Text line = lore.get(i);
      String lineText = line.getString();
      tooltipLines.add(lineText);
      ItemCompare.LOGGER.info("Line " + i + ": '" + lineText + "'");
    }

    // Use common parsing logic
    ItemStatsCommon commonStats = ItemStatsCommon.parseFromTooltip(itemName, tooltipLines);

    // Create Fabric-specific wrapper
    ItemStats stats = new ItemStats(commonStats.getItemName(), commonStats.getRarity(), "UNKNOWN");
    stats.stats.putAll(commonStats.stats);
    stats.percentageStats.addAll(commonStats.percentageStats);
    stats.enchantments.putAll(commonStats.enchantments);

    ItemCompare.LOGGER.info("Final parsed stats: " + stats.stats);
    ItemCompare.LOGGER.info("=== END PARSING ===");

    return stats;
  }
}
