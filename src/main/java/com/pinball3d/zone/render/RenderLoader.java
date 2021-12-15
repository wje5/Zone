package com.pinball3d.zone.render;

import com.pinball3d.zone.tileentity.TECastingTable;
import com.pinball3d.zone.tileentity.TECrucible;
import com.pinball3d.zone.tileentity.TEPoweredPiston;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class RenderLoader {
	public RenderLoader() {
		ClientRegistry.bindTileEntitySpecialRenderer(TECrucible.class, new RenderCrucible());
		ClientRegistry.bindTileEntitySpecialRenderer(TECastingTable.class, new RenderCastingTable());
		ClientRegistry.bindTileEntitySpecialRenderer(TEPoweredPiston.class, new RenderPoweredPiston());
	}
}
