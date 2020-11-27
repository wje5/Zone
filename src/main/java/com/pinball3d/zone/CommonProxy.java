package com.pinball3d.zone;

import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.network.ConnectionHelper;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.VanillaRecipeHandler;
import com.pinball3d.zone.tileentity.TileEntityLoader;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public static ConnectionHelper instance;

	public void preInit(FMLPreInitializationEvent event) {
		new ConfigLoader(event);
		new CapabilityLoader(event);
		TabZone.init();
		new ChunkHandler();
		TileEntityLoader.init();
		new NetworkHandler();
	}

	public void init(FMLInitializationEvent event) {
		RecipeHandler.init();
		VanillaRecipeHandler.init();
		new GuiElementLoader();
	}

	public void postInit(FMLPostInitializationEvent event) {
		if (event.getSide().isServer()) {
			instance = new ConnectionHelper();
		}
	}
}
