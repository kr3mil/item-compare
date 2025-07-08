package com.hypixelcompare;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HypixelCompareClient implements ClientModInitializer {
    
    private static KeyBinding compareKey;
    private static KeyBinding resetKey;
    
    @Override
    public void onInitializeClient() {
        HypixelCompare.LOGGER.info("Hypixel Compare client initialized!");
        
        compareKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hypixel-compare.compare",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.hypixel-compare.general"
        ));
        
        resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hypixel-compare.reset",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.hypixel-compare.general"
        ));
        
        // Handle key presses outside of inventory screens
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (compareKey.wasPressed()) {
                if (client.currentScreen == null) {
                    HypixelCompare.LOGGER.info("Compare key pressed! Current screen: null");
                    ItemComparator.selectHoveredItem();
                }
                // Note: Inventory screen key presses are handled by ScreenKeyboardEvents
            }
            while (resetKey.wasPressed()) {
                HypixelCompare.LOGGER.info("Reset key pressed!");
                ItemComparator.resetSelection();
            }
        });
        
        // Handle key presses in inventory screens using ScreenKeyboardEvents
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof HandledScreen) {
                ScreenKeyboardEvents.beforeKeyPress(screen).register((scrn, key, scancode, modifiers) -> {
                    if (key == GLFW.GLFW_KEY_M) {
                        HypixelCompare.LOGGER.info("M key pressed in inventory! Screen: " + 
                            scrn.getClass().getSimpleName());
                        ItemComparator.selectHoveredItem();
                    }
                });
            }
        });
    }
}