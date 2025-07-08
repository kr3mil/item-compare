package com.hypixelcompare;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ItemComparisonScreen extends Screen {
    private final ItemStack firstItem;
    private final ItemStack secondItem;
    private final SkyblockItemStats firstStats;
    private final SkyblockItemStats secondStats;
    
    public ItemComparisonScreen(ItemStack firstItem, ItemStack secondItem) {
        super(Text.translatable("text.hypixel-compare.comparing"));
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.firstStats = SkyblockItemStats.parseItem(firstItem);
        this.secondStats = SkyblockItemStats.parseItem(secondItem);
    }
    
    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            this.close();
        }).dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        int centerX = this.width / 2;
        int startY = 30;
        
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 10, 0xFFFFFF);
        
        renderItemComparison(context, centerX - 150, startY, firstItem, firstStats, "First Item");
        renderItemComparison(context, centerX + 50, startY, secondItem, secondStats, "Second Item");
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderItemComparison(DrawContext context, int x, int y, ItemStack item, 
                                    SkyblockItemStats stats, String title) {
        context.drawTextWithShadow(this.textRenderer, title, x, y, 0xFFFFFF);
        
        context.drawItem(item, x, y + 15);
        context.drawTextWithShadow(this.textRenderer, item.getName(), x + 20, y + 20, 0xFFFFFF);
        
        int currentY = y + 40;
        
        for (String stat : stats.getFormattedStats()) {
            context.drawTextWithShadow(this.textRenderer, stat, x, currentY, 0xAAAAAA);
            currentY += 12;
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}