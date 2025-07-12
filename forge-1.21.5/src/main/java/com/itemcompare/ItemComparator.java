package com.itemcompare;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemComparator {
  private static ItemStack firstItem = null;

  public static void selectHoveredItem() {
    Minecraft client = Minecraft.getInstance();
    if (client.player == null) {
      ItemCompare.LOGGER.info("selectHoveredItem: player is null");
      return;
    }

    ItemCompare.LOGGER.info("selectHoveredItem called");
    client.player.displayClientMessage(Component.literal("§bselectHoveredItem called"), false);

    // Get the currently hovered item
    ItemStack hoveredItem = getHoveredItem();
    ItemCompare.LOGGER.info(
        "Hovered item: {}", hoveredItem != null ? hoveredItem.toString() : "null");

    if (hoveredItem == null || hoveredItem.isEmpty()) {
      client.player.displayClientMessage(Component.literal("§cNo item hovered"), false);
      ItemCompare.LOGGER.info("No item hovered");
      return;
    }

    client.player.displayClientMessage(
        Component.literal("§aHovered item: " + hoveredItem.getDisplayName().getString()), false);

    if (firstItem == null) {
      // Select first item
      firstItem = hoveredItem.copy();
      client.player.displayClientMessage(
          Component.literal("§eFirst item selected: " + hoveredItem.getDisplayName().getString()),
          false);
      ItemCompare.LOGGER.info("First item selected: {}", hoveredItem.getDisplayName().getString());
    } else {
      // Select second item and show comparison
      ItemStack secondItem = hoveredItem.copy();
      client.player.displayClientMessage(
          Component.literal("§eSecond item selected: " + hoveredItem.getDisplayName().getString()),
          false);
      ItemCompare.LOGGER.info(
          "Second item selected: {}, opening comparison", hoveredItem.getDisplayName().getString());
      showComparison(firstItem, secondItem);
      firstItem = null; // Reset for next comparison
    }
  }

  private static ItemStack getHoveredItem() {
    Minecraft client = Minecraft.getInstance();
    if (client.screen instanceof AbstractContainerScreen<?> containerScreen) {
      double mouseX =
          client.mouseHandler.xpos()
              * client.getWindow().getGuiScaledWidth()
              / client.getWindow().getScreenWidth();
      double mouseY =
          client.mouseHandler.ypos()
              * client.getWindow().getGuiScaledHeight()
              / client.getWindow().getScreenHeight();

      // Use reflection to access the private getSlotUnderMouse method
      try {
        java.lang.reflect.Method getSlotMethod =
            AbstractContainerScreen.class.getDeclaredMethod("getSlotUnderMouse");
        getSlotMethod.setAccessible(true);
        Slot hoveredSlot = (Slot) getSlotMethod.invoke(containerScreen);

        if (hoveredSlot != null && hoveredSlot.hasItem()) {
          return hoveredSlot.getItem();
        }
      } catch (Exception e) {
        // Try alternative method names that might exist
        for (java.lang.reflect.Method method : AbstractContainerScreen.class.getDeclaredMethods()) {
          if (method.getParameterCount() == 0 && method.getReturnType() == Slot.class) {
            try {
              method.setAccessible(true);
              Slot slot = (Slot) method.invoke(containerScreen);
              if (slot != null && slot.hasItem()) {
                return slot.getItem();
              }
            } catch (Exception ex) {
              // Continue trying other methods
            }
          }
        }
      }
    }
    return null;
  }

  private static void showComparison(ItemStack first, ItemStack second) {
    Minecraft.getInstance().setScreen(new ItemComparisonScreen(first, second));
  }
}
