package com.pinball3d.zone.sphinx;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Util {
	public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
		drawTexture(texture, x, y, 0, 0, width, height, 1.0F);
	}

	public static void drawTexture(ResourceLocation texture, int x, int y, int uWidth, int vHeight, float scale) {
		drawTexture(texture, x, y, 0, 0, uWidth, vHeight, scale);
	}

	public static void drawTexture(ResourceLocation texture, int x, int y, int u, int v, int uWidth, int vHeight,
			float scale) {
		drawTexture(texture, x, y, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight);
	}

	public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height, int u, int v,
			int uWidth, int vHeight) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 0.00390625F;
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, 0).tex(u * f, (v + vHeight) * f).endVertex();
		bufferbuilder.pos(x + width, y + height, 0).tex((u + uWidth) * f, (v + vHeight) * f).endVertex();
		bufferbuilder.pos(x + width, y, 0).tex((u + uWidth) * f, v * f).endVertex();
		bufferbuilder.pos(x, y, 0).tex(u * f, v * f).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
	}
}
