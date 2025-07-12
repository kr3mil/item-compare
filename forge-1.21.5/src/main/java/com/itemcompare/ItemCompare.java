package com.itemcompare;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ItemCompare.MOD_ID)
public class ItemCompare {
  public static final String MOD_ID = "item-compare";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static KeyMapping compareKeyBinding;

  public ItemCompare(IEventBus modEventBus) {
    LOGGER.info("ItemCompare mod initializing...");

    // Register client setup
    modEventBus.addListener(this::clientSetup);
    modEventBus.addListener(this::registerKeyMappings);
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    LOGGER.info("ItemCompare client setup complete!");

    // Register key press event handler
    NeoForge.EVENT_BUS.register(new ClientEventHandler());
  }

  private void registerKeyMappings(final RegisterKeyMappingsEvent event) {
    compareKeyBinding =
        new KeyMapping(
            "key.item-compare.compare", GLFW.GLFW_KEY_M, "category.item-compare.general");
    event.register(compareKeyBinding);
    LOGGER.info("Registered keybinding: M key (GLFW code: {})", GLFW.GLFW_KEY_M);
    LOGGER.info("KeyMapping object created: {}", compareKeyBinding);
  }

  @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
  public static class ClientEventHandler {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onKeyPress(ScreenEvent.KeyPressed.Pre event) {
      Minecraft client = Minecraft.getInstance();

      // BASIC DEBUG: Log ALL key presses regardless of screen
      LOGGER.info(
          "ScreenEvent.KeyPressed.Pre FIRED! keyCode: {}, scanCode: {}, screen: {}",
          event.getKeyCode(),
          event.getScanCode(),
          client.screen != null ? client.screen.getClass().getSimpleName() : "null");

      // Always send chat message for ANY key press so we can see if events work at all
      if (client.player != null) {
        client.player.displayClientMessage(
            net.minecraft.network.chat.Component.literal(
                "§9ScreenEvent.KeyPressed.Pre: " + event.getKeyCode() + " (any screen)"),
            false);
      }

      // Debug: Log all key presses when in container screens
      if (client.screen instanceof AbstractContainerScreen) {
        LOGGER.info(
            "In container screen - keyCode: {}, scanCode: {}",
            event.getKeyCode(),
            event.getScanCode());

        if (client.player != null) {
          client.player.displayClientMessage(
              net.minecraft.network.chat.Component.literal(
                  "§7In container - key: " + event.getKeyCode() + " (M = " + GLFW.GLFW_KEY_M + ")"),
              false);
        }

        // Check if it's M key by raw code
        if (event.getKeyCode() == GLFW.GLFW_KEY_M) {
          if (client.player != null) {
            client.player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§aRaw M key detected!"), false);
          }
        }
      }

      // Check if it's our compare key
      if (compareKeyBinding != null
          && compareKeyBinding.matches(event.getKeyCode(), event.getScanCode())) {
        LOGGER.info(
            "ScreenEvent: Compare key binding matched! Screen: {}",
            client.screen != null ? client.screen.getClass().getSimpleName() : "null");

        if (client.player != null) {
          client.player.displayClientMessage(
              net.minecraft.network.chat.Component.literal("§eScreenEvent: Compare key pressed!"),
              false);
        }

        if (client.screen instanceof AbstractContainerScreen) {
          LOGGER.info("Calling ItemComparator.selectHoveredItem()");
          ItemComparator.selectHoveredItem();
          event.setCanceled(true);
        }
      }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
      Minecraft client = Minecraft.getInstance();

      tickCounter++;

      // Send a tick message every 100 ticks (5 seconds) to verify ticking works
      if (tickCounter % 100 == 0 && client.player != null) {
        client.player.displayClientMessage(
            net.minecraft.network.chat.Component.literal(
                "§6Tick " + tickCounter + " - Events working"),
            false);
      }

      // Check if the key binding was pressed during this tick
      if (compareKeyBinding != null && compareKeyBinding.consumeClick()) {
        LOGGER.info(
            "ClientTick: Compare key binding pressed! Screen: {}",
            client.screen != null ? client.screen.getClass().getSimpleName() : "null");

        if (client.player != null) {
          client.player.displayClientMessage(
              net.minecraft.network.chat.Component.literal("§eClientTick: Compare key pressed!"),
              false);
        }

        if (client.screen instanceof AbstractContainerScreen) {
          LOGGER.info("In container screen, calling ItemComparator.selectHoveredItem()");
          ItemComparator.selectHoveredItem();
        } else {
          if (client.player != null) {
            client.player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§cNot in inventory screen"), false);
          }
        }
      }
    }
  }
}
