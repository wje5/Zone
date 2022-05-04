package com.pinball3d.zone.render;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.tileentity.TECableBasic;
import com.pinball3d.zone.tileentity.TECableGeneral;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TEPoweredPiston.class, new RenderPoweredPiston());
		ClientRegistry.bindTileEntitySpecialRenderer(TECableBasic.class, new TESRCableBasic());
		ClientRegistry.bindTileEntitySpecialRenderer(TECableGeneral.class, new TESRCableBasic());
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomMeshDefinition(FluidHandler.fluid, stack -> ModelFluid.LOCATION);
		ModelBakery.registerItemVariants(FluidHandler.fluid, ModelFluid.LOCATION);
	}
}
