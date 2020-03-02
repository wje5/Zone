package com.pinball3d.zone;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Zone.MODID, name = Zone.NAME, version = Zone.VERSION)
public class Zone {
	public static final String MODID = "zone";
	public static final String NAME = "Zone";
	public static final String VERSION = "1.0.4";

	@Mod.Instance(Zone.MODID)
	public static Zone instance;

	@SidedProxy(clientSide = "com.pinball3d.zone.ClientProxy", serverSide = "com.pinball3d.zone.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
