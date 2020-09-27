package com.pinball3d.zone.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TEISRCrucible extends TileEntityItemStackRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/blocks/crucible.png");
	private ModelCrucible model = new ModelCrucible();

	@Override
	public void renderByItem(ItemStack stack, float ticks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.3F, 0.5F);
		GlStateManager.rotate(135.0F, -1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(0.625F, 0.625F, 0.625F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GlStateManager.popMatrix();
		super.renderByItem(stack, ticks);
	}
}
