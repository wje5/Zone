package com.pinball3d.zone;

import com.pinball3d.zone.gui.GuiElementLoader;
import com.pinball3d.zone.tileentity.TileEntityLoader;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		TabZone.init();
		TileEntityLoader.init();
	}

	public void init(FMLInitializationEvent event) {
		new GuiElementLoader();
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
