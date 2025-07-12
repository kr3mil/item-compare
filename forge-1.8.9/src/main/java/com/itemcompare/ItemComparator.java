package com.itemcompare;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class ItemComparator {
  private static ItemStack firstItem = null;

  public static void selectHoveredItem() {
    Minecraft client = Minecraft.getMinecraft();
    if (client.thePlayer == null) {
      ItemCompare.logger.info("selectHoveredItem: player is null");
      return;
    }

    ItemCompare.logger.info("selectHoveredItem called");
    client.thePlayer.addChatMessage(new ChatComponentText("§bselectHoveredItem called"));

    // Get the currently hovered item
    ItemStack hoveredItem = getHoveredItem();
    ItemCompare.logger.info("Hovered item: {}", hoveredItem != null ? hoveredItem.toString() : "null");

    if (hoveredItem == null) {
      client.thePlayer.addChatMessage(new ChatComponentText("§cNo item hovered"));
      ItemCompare.logger.info("No item hovered");
      return;
    }

    client.thePlayer.addChatMessage(new ChatComponentText("§aHovered item: " + hoveredItem.getDisplayName()));

    if (firstItem == null) {
      // Select first item
      firstItem = hoveredItem.copy();
      client.thePlayer.addChatMessage(
          new ChatComponentText("§eFirst item selected: " + hoveredItem.getDisplayName()));
      ItemCompare.logger.info("First item selected: {}", hoveredItem.getDisplayName());
    } else {
      // Select second item and show comparison
      ItemStack secondItem = hoveredItem.copy();
      client.thePlayer.addChatMessage(
          new ChatComponentText("§eSecond item selected: " + hoveredItem.getDisplayName()));
      ItemCompare.logger.info("Second item selected: {}, opening comparison", hoveredItem.getDisplayName());
      showComparison(firstItem, secondItem);
      firstItem = null; // Reset for next comparison
    }
  }

  private static ItemStack getHoveredItem() {
    Minecraft client = Minecraft.getMinecraft();
    if (client.currentScreen instanceof GuiContainer) {
      GuiContainer guiContainer = (GuiContainer) client.currentScreen;
      
      // Use reflection to access the private theSlot field
      try {
        java.lang.reflect.Field theSlotField = GuiContainer.class.getDeclaredField("theSlot");
        theSlotField.setAccessible(true);
        Slot hoveredSlot = (Slot) theSlotField.get(guiContainer);

        if (hoveredSlot != null && hoveredSlot.getHasStack()) {
          return hoveredSlot.getStack();
        }
      } catch (Exception e) {
        ItemCompare.logger.error("Error getting hovered slot: " + e.getMessage());
        
        // Try alternative field names that might exist in different mappings
        try {
          java.lang.reflect.Field[] fields = GuiContainer.class.getDeclaredFields();
          for (java.lang.reflect.Field field : fields) {
            if (field.getType() == Slot.class) {
              field.setAccessible(true);
              Slot slot = (Slot) field.get(guiContainer);
              if (slot != null && slot.getHasStack()) {
                return slot.getStack();
              }
            }
          }
        } catch (Exception ex) {
          ItemCompare.logger.error("Error with alternative slot access: " + ex.getMessage());
        }
      }
    }
    return null;
  }

  private static void showComparison(ItemStack first, ItemStack second) {
    Minecraft.getMinecraft().displayGuiScreen(new ItemComparisonScreen(first, second));
  }
}