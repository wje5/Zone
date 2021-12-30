package com.pinball3d.zone.render;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.tileentity.TECastingTable;
import com.pinball3d.zone.tileentity.TECrucible;
import com.pinball3d.zone.tileentity.TEPoweredPiston;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RenderLoader {
	public RenderLoader() {
		ModelLoaderRegistry.registerLoader(ModelFluid.LoaderDynBucket.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TECrucible.class, new RenderCrucible());
		ClientRegistry.bindTileEntitySpecialRenderer(TECastingTable.class, new RenderCastingTable());
		ClientRegistry.bindTileEntitySpecialRenderer(TEPoweredPiston.class, new RenderPoweredPiston());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomMeshDefinition(FluidHandler.fluid, stack -> ModelFluid.LOCATION);
		ModelBakery.registerItemVariants(FluidHandler.fluid, ModelFluid.LOCATION);
	}
}
