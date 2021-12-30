package com.pinball3d.zone.render;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class TEISRFluid extends TileEntityItemStackRenderer {
	@Override
	public void renderByItem(ItemStack stack, float ticks) {
//		Fluid fluid = ((ItemFluid) stack.getItem()).fluid;
//		TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
//		ResourceLocation fluidStill = fluid.getStill();
//		TextureAtlasSprite fluidStillSprite = null;
//		if (fluidStill != null) {
//			fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
//		}
//		if (fluidStillSprite != null) {
//			fluidStillSprite = textureMapBlocks.getMissingSprite();
//		}
//
//		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
////		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
////		Minecraft.getMinecraft().getTextureManager().bindTexture(EliteMainwindow.ELITE);
//
//		int color = fluid.getColor(new FluidStack(fluid, 1000));
//		GlStateManager.color((color >> 24 & 255) / 255F, (color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F,
//				(color & 255) / 255F);
//		GlStateManager.color(1, 1, 1, 1);
//		double uMin = fluidStillSprite.getMinU();
//		double uMax = fluidStillSprite.getMaxU();
//		double vMin = fluidStillSprite.getMinV();
//		double vMax = fluidStillSprite.getMaxV();
//		uMin = vMin = 0;
//		uMax = vMax = 1;
//		int maskTop = 0, maskRight = 0;
//
//		GlStateManager.enableTexture2D();
//		GlStateManager.enableBlend();
//		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
//				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
//				GlStateManager.DestFactor.ZERO);
//
//		GlStateManager.pushMatrix();
//		GlStateManager.rotate(90, 1, 0, 0);
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder bufferBuilder = tessellator.getBuffer();
//		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//		bufferBuilder.pos(0, 1, 100).tex(uMin, vMax).endVertex();
//		bufferBuilder.pos(1 - maskRight, 1, 100).tex(uMax, vMax).endVertex();
//		bufferBuilder.pos(1 - maskRight, maskTop, 100).tex(uMax, vMin).endVertex();
//		bufferBuilder.pos(0, maskTop, 100).tex(uMin, vMin).endVertex();
//		tessellator.draw();
//		EliteRenderHelper.drawRect(0, 0, 1, 1, Color.RED);
//		GlStateManager.popMatrix();
//		EliteRenderHelper.drawRect(0, 0, 1, 1, Color.RED);
//		super.renderByItem(stack, ticks);
	}
}
