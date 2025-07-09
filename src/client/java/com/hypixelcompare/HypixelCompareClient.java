package com.hypixelcompare;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.lwjgl.glfw.GLFW;

public class HypixelCompareClient implements ClientModInitializer {
    
    
    @Override
    public void onInitializeClient() {
        HypixelCompare.LOGGER.info("Hypixel Compare client initialized!");
        
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