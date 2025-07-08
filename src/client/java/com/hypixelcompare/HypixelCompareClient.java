package com.hypixelcompare;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HypixelCompareClient implements ClientModInitializer {
    
    private static KeyBinding compareKey;
    
    @Override
    public void onInitializeClient() {
        HypixelCompare.LOGGER.info("Hypixel Compare client initialized!");
        
        compareKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hypixel-compare.compare",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.hypixel-compare.general"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (compareKey.wasPressed()) {
                ItemComparator.toggleComparison();
            }
        });
    }
}