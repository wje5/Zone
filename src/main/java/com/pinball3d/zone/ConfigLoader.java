package com.pinball3d.zone;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigLoader {
	private static Configuration config;
	private static Logger logger;
	public static int chunkUpdateRate, mapUpdateRate, packUpdateRate, itemUpdateRate, classifyUpdateRate, logUpdateRate,
			sphinxLogCache;
	public static boolean disableMachineSound;

	public ConfigLoader(FMLPreInitializationEvent event) {
		if (!ForgeChunkManager.getConfig().hasCategory("zone")) {
			ForgeChunkManager.getConfig().get("zone", "maximumChunksPerTicket", Integer.MAX_VALUE).setMinValue(0);
			ForgeChunkManager.getConfig().get("zone", "maximumTicketCount", Integer.MAX_VALUE).setMinValue(0);
			ForgeChunkManager.getConfig().save();
		}
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		load();
	}

	public static void load() {
		logger.info("Started loading config.");
		chunkUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "chunkUpdateRate", 60, "").getInt();
		mapUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "mapUpdateRate", 10, "").getInt();
		packUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "packUpdateRate", 1, "").getInt();
		itemUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "itemUpdateRate", 10, "").getInt();
		classifyUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "classifyUpdateRate", 15, "").getInt();
		logUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "logUpdateRate", 10, "").getInt();
		sphinxLogCache = config.get(Configuration.CATEGORY_GENERAL, "sphinxLogCache", 100, "").getInt();
		disableMachineSound = config.get(Configuration.CATEGORY_GENERAL, "disableMachineSound", false).getBoolean();
		config.save();
		logger.info("Finished loading config.");
	}

	public static Logger getLogger() {
		return logger;
	}
}
