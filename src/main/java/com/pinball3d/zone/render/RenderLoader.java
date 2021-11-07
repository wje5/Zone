package com.pinball3d.zone.render;

import com.pinball3d.zone.entity.EntityPiano;
import com.pinball3d.zone.render.entity.RenderPiano;
import com.pinball3d.zone.tileentity.TECastingTable;
import com.pinball3d.zone.tileentity.TECrucible;
import com.pinball3d.zone.tileentity.TEPoweredPiston;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderLoader {
	public RenderLoader() {
		ClientRegistry.bindTileEntitySpecialRenderer(TECrucible.class, new RenderCrucible());
		ClientRegistry.bindTileEntitySpecialRenderer(TECastingTable.class, new RenderCastingTable());
		ClientRegistry.bindTileEntitySpecialRenderer(TEPoweredPiston.class, new RenderPoweredPiston());

		RenderingRegistry.registerEntityRenderingHandler(EntityPiano.class, manager -> new RenderPiano(manager));
	}
}
