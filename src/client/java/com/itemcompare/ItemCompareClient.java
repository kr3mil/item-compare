package com.itemcompare;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ItemCompareClient implements ClientModInitializer {
  private static KeyBinding compareKeyBinding;

  @Override
  public void onInitializeClient() {
    ItemCompare.LOGGER.info("Item Compare client initialized!");

    // Register the configurable keybind
    compareKeyBinding =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.item-compare.compare",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.item-compare.general"));

    // Handle keybind presses in inventory screens using ScreenKeyboardEvents
    ScreenEvents.BEFORE_INIT.register(
        (client, screen, scaledWidth, scaledHeight) -> {
          if (screen instanceof HandledScreen) {
            ScreenKeyboardEvents.beforeKeyPress(screen)
                .register(
                    (scrn, key, scancode, modifiers) -> {
                      if (compareKeyBinding.matchesKey(key, scancode)) {
                        ItemCompare.LOGGER.info(
                            "Compare key pressed in inventory! Screen: "
                                + scrn.getClass().getSimpleName());
                        ItemComparator.selectHoveredItem();
                      }
                    });
          }
        });
  }
}
