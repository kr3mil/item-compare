package com.hypixelcompare;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemComparator {
    private static boolean comparisonMode = false;
    private static ItemStack firstItem = null;
    private static ItemStack secondItem = null;
    
    public static void toggleComparison() {
        comparisonMode = !comparisonMode;
        
        if (comparisonMode) {
            firstItem = null;
            secondItem = null;
            MinecraftClient.getInstance().player.sendMessage(
                Text.translatable("text.hypixel-compare.select_first"), false);
        } else {
            MinecraftClient.getInstance().player.sendMessage(
                Text.translatable("text.hypixel-compare.comparison_mode"), false);
        }
    }
    
    public static boolean isComparisonMode() {
        return comparisonMode;
    }
    
    public static void selectItem(ItemStack item) {
        if (!comparisonMode) return;
        
        if (firstItem == null) {
            firstItem = item.copy();
            MinecraftClient.getInstance().player.sendMessage(
                Text.translatable("text.hypixel-compare.select_second"), false);
        } else if (secondItem == null) {
            secondItem = item.copy();
            showComparison();
        }
    }
    
    private static void showComparison() {
        if (firstItem != null && secondItem != null) {
            MinecraftClient.getInstance().setScreen(
                new ItemComparisonScreen(firstItem, secondItem));
            comparisonMode = false;
            firstItem = null;
            secondItem = null;
        }
    }
}