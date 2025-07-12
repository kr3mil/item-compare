package com.itemcompare;

import com.itemcompare.util.ColorUtils;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemComparisonScreen extends Screen {
  private final ItemStack firstItem;
  private final ItemStack secondItem;
  private final ItemStats firstStats;
  private final ItemStats secondStats;

  public ItemComparisonScreen(ItemStack firstItem, ItemStack secondItem) {
    super(Component.translatable("text.item-compare.comparing"));
    this.firstItem = firstItem;
    this.secondItem = secondItem;

    ItemCompare.LOGGER.info("=== CREATING COMPARISON SCREEN ===");
    ItemCompare.LOGGER.info("First item: " + firstItem.getDisplayName().getString());
    ItemCompare.LOGGER.info("Second item: " + secondItem.getDisplayName().getString());

    this.firstStats = ItemStats.parseItem(firstItem);
    this.secondStats = ItemStats.parseItem(secondItem);

    ItemCompare.LOGGER.info("First stats count: " + firstStats.stats.size());
    ItemCompare.LOGGER.info("Second stats count: " + secondStats.stats.size());
  }

  @Override
  protected void init() {
    this.addRenderableWidget(
        Button.builder(
                Component.literal("Close"),
                button -> {
                  this.onClose();
                })
            .bounds(this.width / 2 - 50, this.height - 30, 100, 20)
            .build());
  }

  @Override
  public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    // Override to prevent any background blur - do nothing
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    // Draw a solid dark background - no blur
    guiGraphics.fill(0, 0, this.width, this.height, 0xC0000000);

    int centerX = this.width / 2;
    int padding = 20;
    int titleHeight = 25;
    int headerHeight = 60;

    // Draw main comparison panel with proper padding
    int panelWidth = Math.min(700, this.width - 40);
    int panelHeight = this.height - 60;
    int panelX = centerX - panelWidth / 2;
    int panelY = 20;

    // Draw panel with rounded corners effect
    guiGraphics.fill(
        panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFF555555);
    guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF0000000);

    // Title with better spacing
    String titleText =
        "§6Item Comparison: §f"
            + firstItem.getDisplayName().getString()
            + " §7vs §f"
            + secondItem.getDisplayName().getString();
    guiGraphics.drawCenteredString(this.font, titleText, centerX, panelY + 12, 0xFFFFFF);

    // Separator line under title
    int separatorY = panelY + titleHeight + 5;
    guiGraphics.fill(panelX + 20, separatorY, panelX + panelWidth - 20, separatorY + 1, 0xFF666666);

    // Item headers with better positioning
    int itemHeaderY = separatorY + 15;
    renderItemHeader(
        guiGraphics, centerX - 180, itemHeaderY, firstItem, firstStats, "§eCurrent Item");
    renderItemHeader(
        guiGraphics, centerX + 80, itemHeaderY, secondItem, secondStats, "§eCompared Item");

    // Stats section with proper spacing
    int statsY = itemHeaderY + headerHeight + 10;
    int enchantY = renderStatComparison(guiGraphics, centerX, statsY);
    renderEnchantmentComparison(guiGraphics, centerX, enchantY + 15);

    // Don't call super.render() to avoid any blur effects
    // Instead, manually render the widgets
    super.render(guiGraphics, mouseX, mouseY, partialTick);
  }

  private void renderItemHeader(
      GuiGraphics guiGraphics, int x, int y, ItemStack item, ItemStats stats, String title) {
    // Title with better styling
    guiGraphics.drawString(this.font, title, x, y, 0xFFFF55, true);

    // Item icon with background
    guiGraphics.fill(x - 2, y + 17, x + 18, y + 37, 0xFF333333);
    guiGraphics.renderItem(item, x, y + 19);

    // Item name with proper spacing
    guiGraphics.drawString(this.font, item.getDisplayName(), x + 22, y + 22, 0xFFFFFF, true);

    // Rarity with better formatting
    String rarity =
        "§7Rarity: §" + ColorUtils.getRarityColor(stats.getRarity()) + stats.getRarity();
    guiGraphics.drawString(this.font, rarity, x + 22, y + 35, 0xFFFFFF, true);
  }

  private int renderStatComparison(GuiGraphics guiGraphics, int centerX, int startY) {
    // Section header with separator
    guiGraphics.drawCenteredString(this.font, "§e§lSTAT COMPARISON", centerX, startY, 0xFFFF55);
    guiGraphics.fill(centerX - 100, startY + 12, centerX + 100, startY + 13, 0xFF666666);

    int currentY = startY + 25;

    // Get all unique stats from both items
    Set<String> allStats = new HashSet<>();
    allStats.addAll(firstStats.stats.keySet());
    allStats.addAll(secondStats.stats.keySet());

    // Sort stats for consistent display
    List<String> sortedStats = new ArrayList<>(allStats);
    sortedStats.sort(String::compareTo);

    // Display stats with preference for important ones first
    String[] priorityOrder = {
      "Damage",
      "Defense",
      "Strength",
      "Health",
      "Mana",
      "Intelligence",
      "Speed",
      "Mining Speed",
      "Mining Fortune",
      "Breaking Power"
    };

    // Show priority stats first
    for (String stat : priorityOrder) {
      if (allStats.contains(stat)) {
        int firstValue = firstStats.getStat(stat);
        int secondValue = secondStats.getStat(stat);
        currentY +=
            renderStatDifference(guiGraphics, centerX, currentY, stat, firstValue, secondValue);
        sortedStats.remove(stat);
      }
    }

    // Show remaining stats alphabetically
    for (String stat : sortedStats) {
      int firstValue = firstStats.getStat(stat);
      int secondValue = secondStats.getStat(stat);
      currentY +=
          renderStatDifference(guiGraphics, centerX, currentY, stat, firstValue, secondValue);
    }

    return currentY;
  }

  private int renderStatDifference(
      GuiGraphics guiGraphics,
      int centerX,
      int y,
      String statName,
      int firstValue,
      int secondValue) {
    int difference = secondValue - firstValue;
    String statColor = ColorUtils.getStatColor(statName);
    String suffix =
        (firstStats.isPercentageStat(statName) || secondStats.isPercentageStat(statName))
            ? "%"
            : "";

    // Add subtle background for alternating rows
    if ((y / 14) % 2 == 0) {
      guiGraphics.fill(centerX - 170, y - 2, centerX + 170, y + 12, 0x20FFFFFF);
    }

    // Base stat line with proper alignment
    String baseStat = "§7" + statName + ": " + statColor + firstValue + suffix;
    guiGraphics.drawString(this.font, baseStat, centerX - 160, y, 0xFFFFFF, true);

    // Comparison arrow with better styling
    guiGraphics.drawString(this.font, "§8→", centerX - 20, y, 0xFFFFFF, true);

    // New value
    String newValue = statColor + secondValue + suffix;
    guiGraphics.drawString(this.font, newValue, centerX + 5, y, 0xFFFFFF, true);

    // Difference indicator with better positioning
    if (difference != 0) {
      String diffText;
      int diffColor;

      if (difference > 0) {
        diffText = "(+" + difference + suffix + ")";
        diffColor = 0x55FF55; // Green for positive
      } else {
        diffText = "(" + difference + suffix + ")";
        diffColor = 0xFF5555; // Red for negative
      }

      guiGraphics.drawString(this.font, diffText, centerX + 85, y, diffColor, true);
    }

    return 14; // Increased line height for better spacing
  }

  private void renderEnchantmentComparison(GuiGraphics guiGraphics, int centerX, int startY) {
    // Section header with separator
    guiGraphics.drawCenteredString(this.font, "§d§lENCHANTMENTS", centerX, startY, 0xFFAA00);
    guiGraphics.fill(centerX - 100, startY + 12, centerX + 100, startY + 13, 0xFF666666);

    int currentY = startY + 25;

    // Get all unique enchantments from both items
    Set<String> allEnchantments = new HashSet<>();
    allEnchantments.addAll(firstStats.enchantments.keySet());
    allEnchantments.addAll(secondStats.enchantments.keySet());

    if (allEnchantments.isEmpty()) {
      guiGraphics.drawCenteredString(
          this.font, "§7No enchantments found", centerX, currentY, 0xFFFFFF);
      return;
    }

    // Sort enchantments alphabetically
    List<String> sortedEnchantments = new ArrayList<>(allEnchantments);
    sortedEnchantments.sort(String::compareTo);

    // Display enchantments
    for (String enchantment : sortedEnchantments) {
      int firstLevel = firstStats.enchantments.getOrDefault(enchantment, 0);
      int secondLevel = secondStats.enchantments.getOrDefault(enchantment, 0);
      currentY +=
          renderEnchantmentDifference(
              guiGraphics, centerX, currentY, enchantment, firstLevel, secondLevel);
    }
  }

  private int renderEnchantmentDifference(
      GuiGraphics guiGraphics,
      int centerX,
      int y,
      String enchantment,
      int firstLevel,
      int secondLevel) {
    int difference = secondLevel - firstLevel;

    // Add subtle background for alternating rows
    if ((y / 14) % 2 == 0) {
      guiGraphics.fill(centerX - 170, y - 2, centerX + 170, y + 12, 0x20FFFFFF);
    }

    // Base enchantment line with proper alignment
    String baseEnchant =
        "§7" + enchantment + ": §d" + (firstLevel > 0 ? intToRoman(firstLevel) : "§8None");
    guiGraphics.drawString(this.font, baseEnchant, centerX - 160, y, 0xFFFFFF, true);

    // Comparison arrow
    guiGraphics.drawString(this.font, "§8→", centerX - 20, y, 0xFFFFFF, true);

    // New value
    String newValue = "§d" + (secondLevel > 0 ? intToRoman(secondLevel) : "§8None");
    guiGraphics.drawString(this.font, newValue, centerX + 5, y, 0xFFFFFF, true);

    // Difference indicator with better positioning
    if (difference != 0) {
      String diffText;
      int diffColor;

      if (difference > 0) {
        diffText = "(+" + difference + ")";
        diffColor = 0x55FF55; // Green for positive
      } else {
        diffText = "(" + difference + ")";
        diffColor = 0xFF5555; // Red for negative
      }

      guiGraphics.drawString(this.font, diffText, centerX + 85, y, diffColor, true);
    }

    return 14; // Increased line height for consistency
  }

  private String intToRoman(int num) {
    if (num <= 0) return "";
    String[] romanNumerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    if (num < romanNumerals.length) {
      return romanNumerals[num];
    }
    return String.valueOf(num); // Fallback for numbers > 10
  }

  @Override
  public boolean shouldCloseOnEsc() {
    return true;
  }
}
