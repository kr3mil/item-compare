package com.itemcompare;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Mod(modid = ItemCompare.MODID, version = ItemCompare.VERSION)
public class ItemCompare {
    public static final String MODID = "item-compare";
    public static final String VERSION = "0.0.2";
    
    public static Logger logger;
    public static KeyBinding compareKeyBinding;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("ItemCompare mod pre-initializing...");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("ItemCompare mod initializing...");
        
        // Register keybinding
        compareKeyBinding = new KeyBinding(
            "key.item-compare.compare",
            Keyboard.KEY_M,
            "category.item-compare.general"
        );
        ClientRegistry.registerKeyBinding(compareKeyBinding);
        logger.info("Registered keybinding: M key");
        
        // Register event handler
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        logger.info("ItemCompare mod initialization complete!");
    }
    
    public static class KeyInputHandler {
        private int tickCounter = 0;
        
        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event) {
            Minecraft mc = Minecraft.getMinecraft();
            
            // BASIC DEBUG: Log ALL key input events
            logger.info("InputEvent.KeyInputEvent FIRED! Screen: {}", 
                mc.currentScreen != null ? mc.currentScreen.getClass().getSimpleName() : "null");
            
            // Always send chat message to verify events work
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§9InputEvent.KeyInputEvent fired"));
            }
            
            // Debug: Log when any key is pressed
            if (mc.currentScreen instanceof GuiContainer) {
                logger.info("InputEvent.KeyInputEvent fired in container screen");
                if (mc.thePlayer != null) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§7In container screen"));
                }
            }
            
            if (compareKeyBinding != null && compareKeyBinding.isPressed() && mc.currentScreen instanceof GuiContainer) {
                logger.info("InputEvent: Compare key pressed in inventory! Screen: " + mc.currentScreen.getClass().getSimpleName());
                if (mc.thePlayer != null) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§eInputEvent: Compare key pressed!"));
                }
                ItemComparator.selectHoveredItem();
            }
        }
        
        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            
            Minecraft mc = Minecraft.getMinecraft();
            
            tickCounter++;
            
            // Send a tick message every 100 ticks (5 seconds) to verify ticking works
            if (tickCounter % 100 == 0 && mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§6Tick " + tickCounter + " - Events working"));
            }
            
            // Check if the key binding was pressed during this tick
            if (compareKeyBinding != null && compareKeyBinding.isPressed()) {
                logger.info("ClientTick: Compare key binding pressed! Screen: " + 
                    (mc.currentScreen != null ? mc.currentScreen.getClass().getSimpleName() : "null"));
                
                if (mc.thePlayer != null) {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§eClientTick: Compare key pressed!"));
                }
                
                if (mc.currentScreen instanceof GuiContainer) {
                    logger.info("In container screen, calling ItemComparator.selectHoveredItem()");
                    ItemComparator.selectHoveredItem();
                } else {
                    if (mc.thePlayer != null) {
                        mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("§cNot in inventory screen"));
                    }
                }
            }
        }
    }
}