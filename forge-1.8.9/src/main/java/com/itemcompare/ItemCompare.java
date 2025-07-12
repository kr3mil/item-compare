package com.itemcompare;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ItemCompare.MODID, version = ItemCompare.VERSION)
public class ItemCompare {
    public static final String MODID = "item-compare";
    public static final String VERSION = "0.0.2";
    
    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("ItemCompare mod pre-initializing...");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("ItemCompare mod initializing...");
    }
}