package com.pinball3d.zone.sphinx.elite;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class EliteRenderHelper {
	public static void init() {
//		FontHelper.init();
		FontHandler.init();
	}

	public static void drawRect(float x, float y, float width, float height, int color) {
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(x, (double) y + height, 0.0D).endVertex();
		bufferbuilder.pos((double) x + width, (double) y + height, 0.0D).endVertex();
		bufferbuilder.pos((double) x + width, y, 0.0D).endVertex();
		bufferbuilder.pos(x, y, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawTexture(ResourceLocation texture, float x, float y, float u, float v, float uWidth,
			float vHeight) {
		drawTexture(texture, x, y, u, v, uWidth, vHeight, 1.0F);
	}

	public static void drawTexture(ResourceLocation texture, float x, float y, float u, float v, float uWidth,
			float vHeight, float scale) {
		drawTexture(texture, x, y, scale * uWidth, scale * vHeight, u, v, uWidth, vHeight);
	}

	public static void drawTexture(ResourceLocation texture, float x, float y, float width, float height, float u,
			float v, float uWidth, float vHeight) {
		drawTexture(texture, x, y, width, height, u, v, uWidth, vHeight, 0xFFFFFFFF);
	}

	public static void drawTexture(ResourceLocation texture, float x, float y, float width, float height, float u,
			float v, float uWidth, float vHeight, int color) {
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		float f = 0.00390625F;
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, 0).tex(u * f, (v + vHeight) * f).endVertex();
		bufferbuilder.pos(x + width, y + height, 0).tex((u + uWidth) * f, (v + vHeight) * f).endVertex();
		bufferbuilder.pos(x + width, y, 0).tex((u + uWidth) * f, v * f).endVertex();
		bufferbuilder.pos(x, y, 0).tex(u * f, v * f).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
	}

	public static void drawBorder(float x, float y, float width, float height, float lineWidth, int color) {
		drawRect(x, y, width, lineWidth, color);
		drawRect(x, y + height - lineWidth, width, lineWidth, color);
		drawRect(x, y + lineWidth, lineWidth, height - lineWidth, color);
		drawRect(x + width - lineWidth, y + lineWidth, lineWidth, height - lineWidth, color);
	}
}
