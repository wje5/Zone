package com.pinball3d.zone;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigLoader {
	private static Configuration config;
	private static Logger logger;
	public static int mapUpdateRate, packUpdateRate;

	public ConfigLoader(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		load();
	}

	public static void load() {
		logger.info("Started loading config.");
		mapUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "mapUpdateRate", 10, "").getInt();
		packUpdateRate = config.get(Configuration.CATEGORY_GENERAL, "packUpdateRate", 1, "").getInt();
		config.save();
		logger.info("Finished loading config.");
	}

	public static Logger getLogger() {
		return logger;
	}
}
