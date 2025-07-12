package com.itemcompare;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ItemCompare.MOD_ID)
public class ItemCompare {
    public static final String MOD_ID = "item-compare";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ItemCompare(IEventBus modEventBus) {
        LOGGER.info("ItemCompare mod initializing...");
    }
}