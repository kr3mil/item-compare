package com.hypixelcompare;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.*;

public class ItemComparisonScreen extends Screen {
    private final ItemStack firstItem;
    private final ItemStack secondItem;
    private final SkyblockItemStats firstStats;
    private final SkyblockItemStats secondStats;
    
    public ItemComparisonScreen(ItemStack firstItem, ItemStack secondItem) {
        super(Text.translatable("text.hypixel-compare.comparing"));
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        
        HypixelCompare.LOGGER.info("=== CREATING COMPARISON SCREEN ===");
        HypixelCompare.LOGGER.info("First item: " + firstItem.getName().getString());
        HypixelCompare.LOGGER.info("Second item: " + secondItem.getName().getString());
        
        this.firstStats = SkyblockItemStats.parseItem(firstItem);
        this.secondStats = SkyblockItemStats.parseItem(secondItem);
        
        HypixelCompare.LOGGER.info("First stats count: " + firstStats.stats.size());
        HypixelCompare.LOGGER.info("Second stats count: " + secondStats.stats.size());
    }
    
    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            this.close();
        }).dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw a semi-transparent dark background instead of blurred background
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        int centerX = this.width / 2;
        int startY = 30;
        
        // Draw a darker panel for the comparison area
        int panelWidth = 500;
        int panelHeight = 400;
        int panelX = centerX - panelWidth / 2;
        int panelY = startY - 10;
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x90000000);
        
        // Update title to be more descriptive
        String titleText = "§6Item Comparison: §f" + firstItem.getName().getString() + " §7vs §f" + secondItem.getName().getString();
        context.drawCenteredTextWithShadow(this.textRenderer, titleText, centerX, 10, 0xFFFFFF);
        
        renderItemHeader(context, centerX - 200, startY, firstItem, firstStats, "Current Item");
        renderItemHeader(context, centerX + 100, startY, secondItem, secondStats, "Compared Item");
        
        renderStatComparison(context, centerX - 50, startY + 60);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderItemHeader(DrawContext context, int x, int y, ItemStack item, 
                                  SkyblockItemStats stats, String title) {
        context.drawTextWithShadow(this.textRenderer, title, x, y, 0xFFFFFF);
        
        context.drawItem(item, x, y + 15);
        context.drawTextWithShadow(this.textRenderer, item.getName(), x + 20, y + 20, 0xFFFFFF);
        
        String rarity = "§7Rarity: §" + getRarityColor(stats.getRarity()) + stats.getRarity();
        context.drawTextWithShadow(this.textRenderer, rarity, x + 20, y + 35, 0xFFFFFF);
    }
    
    private void renderStatComparison(DrawContext context, int centerX, int startY) {
        context.drawCenteredTextWithShadow(this.textRenderer, "§eSTAT COMPARISON", centerX, startY, 0xFFFF55);
        
        int currentY = startY + 20;
        
        // Get all unique stats from both items
        Set<String> allStats = new HashSet<>();
        allStats.addAll(firstStats.stats.keySet());
        allStats.addAll(secondStats.stats.keySet());
        
        // Sort stats for consistent display
        List<String> sortedStats = new ArrayList<>(allStats);
        sortedStats.sort(String::compareTo);
        
        // Display stats with preference for important ones first
        String[] priorityOrder = {"Damage", "Defense", "Strength", "Health", "Mana", "Intelligence", 
                                "Speed", "Mining Speed", "Mining Fortune", "Breaking Power"};
        
        // Show priority stats first
        for (String stat : priorityOrder) {
            if (allStats.contains(stat)) {
                int firstValue = firstStats.getStat(stat);
                int secondValue = secondStats.getStat(stat);
                currentY += renderStatDifference(context, centerX, currentY, stat, firstValue, secondValue);
                sortedStats.remove(stat);
            }
        }
        
        // Show remaining stats alphabetically
        for (String stat : sortedStats) {
            int firstValue = firstStats.getStat(stat);
            int secondValue = secondStats.getStat(stat);
            currentY += renderStatDifference(context, centerX, currentY, stat, firstValue, secondValue);
        }
    }
    
    private int renderStatDifference(DrawContext context, int centerX, int y, String statName, int firstValue, int secondValue) {
        int difference = secondValue - firstValue;
        String statColor = getStatColor(statName);
        String suffix = (firstStats.isPercentageStat(statName) || secondStats.isPercentageStat(statName)) ? "%" : "";
        
        // Base stat line
        String baseStat = "§7" + statName + ": " + statColor + firstValue + suffix;
        context.drawTextWithShadow(this.textRenderer, baseStat, centerX - 100, y, 0xFFFFFF);
        
        // Comparison arrow
        context.drawTextWithShadow(this.textRenderer, "§7→", centerX - 10, y, 0xFFFFFF);
        
        // New value with difference
        String newValue = statColor + secondValue + suffix;
        context.drawTextWithShadow(this.textRenderer, newValue, centerX + 10, y, 0xFFFFFF);
        
        // Difference indicator
        if (difference != 0) {
            String diffText;
            int diffColor;
            
            if (difference > 0) {
                diffText = " (+" + difference + suffix + ")";
                diffColor = 0x55FF55; // Green for positive
            } else {
                diffText = " (" + difference + suffix + ")";
                diffColor = 0xFF5555; // Red for negative
            }
            
            context.drawTextWithShadow(this.textRenderer, diffText, centerX + 60, y, diffColor);
        }
        
        return 12; // Line height
    }
    
    private String getRarityColor(String rarity) {
        switch (rarity.toUpperCase()) {
            case "COMMON": return "f";
            case "UNCOMMON": return "a";
            case "RARE": return "9";
            case "EPIC": return "5";
            case "LEGENDARY": return "6";
            case "MYTHIC": return "d";
            case "DIVINE": return "b";
            case "SPECIAL": return "c";
            default: return "f";
        }
    }
    
    private String getStatColor(String stat) {
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
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}