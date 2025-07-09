package com.skyblockitemcompare;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyblockItemCompare implements ModInitializer {
    public static final String MOD_ID = "skyblock-item-compare";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Skyblock Item Compare mod initialized!");
    }
}