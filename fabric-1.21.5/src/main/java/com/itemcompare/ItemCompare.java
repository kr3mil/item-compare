package com.itemcompare;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemCompare implements ModInitializer {
  public static final String MOD_ID = "item-compare";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("Item Compare mod initialized!");
  }
}
