package com.pinball3d.zone.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class TEISRDrillHeavy extends TileEntityItemStackRenderer {
	private static final ResourceLocation MODEL = new ResourceLocation("zone:models/item/drill_heavy.obj");

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		IModel model = ModelLoaderRegistry.getModelOrMissing(MODEL);
		IBakedModel bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
				ModelLoader.defaultTextureGetter());
		RenderHelper.disableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.popMatrix();
	}
}
