package com.itemcompare;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class ItemComparator {
  private static ItemStack firstItem = null;

  public static void selectHoveredItem() {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null) return;

    // Get the currently hovered item
    ItemStack hoveredItem = getHoveredItem();

    if (hoveredItem == null || hoveredItem.isEmpty()) {
      client.player.sendMessage(Text.translatable("text.item-compare.no_item_hovered"), false);
      return;
    }

    if (firstItem == null) {
      // Select first item
      firstItem = hoveredItem.copy();
      client.player.sendMessage(
          Text.translatable("text.item-compare.first_selected", hoveredItem.getName()), false);
    } else {
      // Select second item and show comparison
      ItemStack secondItem = hoveredItem.copy();
      showComparison(firstItem, secondItem);
      firstItem = null; // Reset for next comparison
    }
  }

  private static ItemStack getHoveredItem() {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
      double mouseX =
          client.mouse.getX()
              * (double) client.getWindow().getScaledWidth()
              / (double) client.getWindow().getWidth();
      double mouseY =
          client.mouse.getY()
              * (double) client.getWindow().getScaledHeight()
              / (double) client.getWindow().getHeight();

      // Use reflection to access the private getSlotAt method
      try {
        java.lang.reflect.Method getSlotAtMethod =
            HandledScreen.class.getDeclaredMethod("getSlotAt", double.class, double.class);
        getSlotAtMethod.setAccessible(true);
        Slot hoveredSlot = (Slot) getSlotAtMethod.invoke(handledScreen, mouseX, mouseY);

        if (hoveredSlot != null && hoveredSlot.hasStack()) {
          return hoveredSlot.getStack();
        }
      } catch (Exception e) {
        // Try all methods to find the right one
        for (java.lang.reflect.Method method : HandledScreen.class.getDeclaredMethods()) {
          if (method.getParameterCount() == 2
              && method.getParameterTypes()[0] == double.class
              && method.getParameterTypes()[1] == double.class
              && method.getReturnType() == Slot.class) {
            try {
              method.setAccessible(true);
              Slot slot = (Slot) method.invoke(handledScreen, mouseX, mouseY);
              if (slot != null && slot.hasStack()) {
                return slot.getStack();
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
    MinecraftClient.getInstance().setScreen(new ItemComparisonScreen(first, second));
  }
}
