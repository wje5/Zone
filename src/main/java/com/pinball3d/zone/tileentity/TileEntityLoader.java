package com.pinball3d.zone.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLoader {
	public static void init() {
		registerTileEntity(TEDrainer.class, "Drainer");
		registerTileEntity(TEGrinder.class, "Grinder");
	}

	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation("zone", id));
	}
}
