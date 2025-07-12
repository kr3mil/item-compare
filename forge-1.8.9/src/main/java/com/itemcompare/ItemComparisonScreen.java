package com.itemcompare;

import com.itemcompare.util.ColorUtils;
import java.util.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemComparisonScreen extends GuiScreen {
  private final ItemStack firstItem;
  private final ItemStack secondItem;
  private final ItemStats firstStats;
  private final ItemStats secondStats;

  public ItemComparisonScreen(ItemStack firstItem, ItemStack secondItem) {
    this.firstItem = firstItem;
    this.secondItem = secondItem;

    ItemCompare.logger.info("=== CREATING COMPARISON SCREEN ===");
    ItemCompare.logger.info("First item: " + firstItem.getDisplayName());
    ItemCompare.logger.info("Second item: " + secondItem.getDisplayName());

    this.firstStats = ItemStats.parseItem(firstItem);
    this.secondStats = ItemStats.parseItem(secondItem);

    ItemCompare.logger.info("First stats count: " + firstStats.stats.size());
    ItemCompare.logger.info("Second stats count: " + secondStats.stats.size());
  }

  @Override
  public void initGui() {
    this.buttonList.add(new GuiButton(0, this.width / 2 - 50, this.height - 30, 100, 20, "Close"));
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    if (button.id == 0) {
      this.mc.displayGuiScreen(null);
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    // Draw a solid dark background
    this.drawRect(0, 0, this.width, this.height, 0xC0000000);

    int centerX = this.width / 2;
    int padding = 20;
    int titleHeight = 25;
    int headerHeight = 60;

    // Draw main comparison panel with proper padding
    int panelWidth = Math.min(700, this.width - 40);
    int panelHeight = this.height - 60;
    int panelX = centerX - panelWidth / 2;
    int panelY = 20;

    // Draw panel with border effect
    this.drawRect(panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFF555555);
    this.drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF0000000);

    // Title with better spacing
    String titleText = "Item Comparison: " + firstItem.getDisplayName() + " vs " + secondItem.getDisplayName();
    this.drawCenteredString(this.fontRendererObj, titleText, centerX, panelY + 12, 0xFFFFFF);

    // Separator line under title
    int separatorY = panelY + titleHeight + 5;
    this.drawRect(panelX + 20, separatorY, panelX + panelWidth - 20, separatorY + 1, 0xFF666666);

    // Item headers with better positioning
    int itemHeaderY = separatorY + 15;
    renderItemHeader(centerX - 180, itemHeaderY, firstItem, firstStats, "Current Item");
    renderItemHeader(centerX + 80, itemHeaderY, secondItem, secondStats, "Compared Item");

    // Stats section with proper spacing
    int statsY = itemHeaderY + headerHeight + 10;
    int enchantY = renderStatComparison(centerX, statsY);
    renderEnchantmentComparison(centerX, enchantY + 15);

    // Render buttons
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  private void renderItemHeader(int x, int y, ItemStack item, ItemStats stats, String title) {
    // Title
    this.drawString(this.fontRendererObj, title, x, y, 0xFFFF55);

    // Item icon with background
    this.drawRect(x - 2, y + 17, x + 18, y + 37, 0xFF333333);
    
    // Render item icon
    GlStateManager.enableLighting();
    RenderHelper.enableGUIStandardItemLighting();
    this.itemRender.renderItemAndEffectIntoGUI(item, x, y + 19);
    GlStateManager.disableLighting();

    // Item name with proper spacing
    this.drawString(this.fontRendererObj, item.getDisplayName(), x + 22, y + 22, 0xFFFFFF);

    // Rarity with better formatting
    String rarity = "Rarity: " + stats.getRarity();
    this.drawString(this.fontRendererObj, rarity, x + 22, y + 35, 0xFFFFFF);
  }

  private int renderStatComparison(int centerX, int startY) {
    // Section header with separator
    this.drawCenteredString(this.fontRendererObj, "STAT COMPARISON", centerX, startY, 0xFFFF55);
    this.drawRect(centerX - 100, startY + 12, centerX + 100, startY + 13, 0xFF666666);

    int currentY = startY + 25;

    // Get all unique stats from both items
    Set<String> allStats = new HashSet<String>();
    allStats.addAll(firstStats.stats.keySet());
    allStats.addAll(secondStats.stats.keySet());

    // Sort stats for consistent display
    List<String> sortedStats = new ArrayList<String>(allStats);
    Collections.sort(sortedStats);

    // Display stats with preference for important ones first
    String[] priorityOrder = {
      "Damage", "Defense", "Strength", "Health", "Mana", "Intelligence", 
      "Speed", "Mining Speed", "Mining Fortune", "Breaking Power"
    };

    // Show priority stats first
    for (String stat : priorityOrder) {
      if (allStats.contains(stat)) {
        int firstValue = firstStats.getStat(stat);
        int secondValue = secondStats.getStat(stat);
        currentY += renderStatDifference(centerX, currentY, stat, firstValue, secondValue);
        sortedStats.remove(stat);
      }
    }

    // Show remaining stats alphabetically
    for (String stat : sortedStats) {
      int firstValue = firstStats.getStat(stat);
      int secondValue = secondStats.getStat(stat);
      currentY += renderStatDifference(centerX, currentY, stat, firstValue, secondValue);
    }

    return currentY;
  }

  private int renderStatDifference(int centerX, int y, String statName, int firstValue, int secondValue) {
    int difference = secondValue - firstValue;
    String suffix = (firstStats.isPercentageStat(statName) || secondStats.isPercentageStat(statName)) ? "%" : "";

    // Add subtle background for alternating rows
    if ((y / 14) % 2 == 0) {
      this.drawRect(centerX - 170, y - 2, centerX + 170, y + 12, 0x20FFFFFF);
    }

    // Base stat line with proper alignment
    String baseStat = statName + ": " + firstValue + suffix;
    this.drawString(this.fontRendererObj, baseStat, centerX - 160, y, 0xFFFFFF);

    // Comparison arrow
    this.drawString(this.fontRendererObj, "->", centerX - 20, y, 0xFFFFFF);

    // New value
    String newValue = String.valueOf(secondValue) + suffix;
    this.drawString(this.fontRendererObj, newValue, centerX + 5, y, 0xFFFFFF);

    // Difference indicator
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

      this.drawString(this.fontRendererObj, diffText, centerX + 85, y, diffColor);
    }

    return 14; // Line height
  }

  private void renderEnchantmentComparison(int centerX, int startY) {
    // Section header with separator
    this.drawCenteredString(this.fontRendererObj, "ENCHANTMENTS", centerX, startY, 0xFFAA00);
    this.drawRect(centerX - 100, startY + 12, centerX + 100, startY + 13, 0xFF666666);

    int currentY = startY + 25;

    // Get all unique enchantments from both items
    Set<String> allEnchantments = new HashSet<String>();
    allEnchantments.addAll(firstStats.enchantments.keySet());
    allEnchantments.addAll(secondStats.enchantments.keySet());

    if (allEnchantments.isEmpty()) {
      this.drawCenteredString(this.fontRendererObj, "No enchantments found", centerX, currentY, 0xFFFFFF);
      return;
    }

    // Sort enchantments alphabetically
    List<String> sortedEnchantments = new ArrayList<String>(allEnchantments);
    Collections.sort(sortedEnchantments);

    // Display enchantments
    for (String enchantment : sortedEnchantments) {
      Integer firstLevel = firstStats.enchantments.get(enchantment);
      Integer secondLevel = secondStats.enchantments.get(enchantment);
      if (firstLevel == null) firstLevel = 0;
      if (secondLevel == null) secondLevel = 0;
      
      currentY += renderEnchantmentDifference(centerX, currentY, enchantment, firstLevel, secondLevel);
    }
  }

  private int renderEnchantmentDifference(int centerX, int y, String enchantment, int firstLevel, int secondLevel) {
    int difference = secondLevel - firstLevel;

    // Add subtle background for alternating rows
    if ((y / 14) % 2 == 0) {
      this.drawRect(centerX - 170, y - 2, centerX + 170, y + 12, 0x20FFFFFF);
    }

    // Base enchantment line
    String baseEnchant = enchantment + ": " + (firstLevel > 0 ? intToRoman(firstLevel) : "None");
    this.drawString(this.fontRendererObj, baseEnchant, centerX - 160, y, 0xFFFFFF);

    // Comparison arrow
    this.drawString(this.fontRendererObj, "->", centerX - 20, y, 0xFFFFFF);

    // New value
    String newValue = (secondLevel > 0 ? intToRoman(secondLevel) : "None");
    this.drawString(this.fontRendererObj, newValue, centerX + 5, y, 0xFFFFFF);

    // Difference indicator
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

      this.drawString(this.fontRendererObj, diffText, centerX + 85, y, diffColor);
    }

    return 14; // Line height
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
  public boolean doesGuiPauseGame() {
    return false;
  }
}