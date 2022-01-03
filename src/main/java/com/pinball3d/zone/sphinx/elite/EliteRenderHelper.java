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
		FontHandler.init();
	}

	public static void drawRect(int x, int y, int width, int height, Color color) {
		float a = color.a / 255.0F;
		float r = color.r / 255.0F;
		float g = color.g / 255.0F;
		float b = color.b / 255.0F;
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

	public static void drawTexture(TextureLocation loc, int x, int y) {
		drawTexture(loc, x, y, 1.0F);
	}

	public static void drawTexture(TextureLocation loc, int x, int y, float scale) {
		drawTexture(loc, x, y, scale * loc.uWidth, scale * loc.vHeight);
	}

	public static void drawTexture(TextureLocation loc, int x, int y, float width, float height) {
		drawTexture(loc.location, x, y, width, height, loc.u, loc.v, loc.uWidth, loc.vHeight);
	}

	public static void drawTexture(ResourceLocation texture, int x, int y, float u, float v, float uWidth,
			float vHeight) {
		drawTexture(texture, x, y, u, v, uWidth, vHeight, 1.0F);
	}

	public static void drawTexture(ResourceLocation texture, int x, int y, float u, float v, float uWidth,
			float vHeight, float scale) {
		drawTexture(texture, x, y, scale * uWidth, scale * vHeight, u, v, uWidth, vHeight);
	}

	public static void drawTexture(ResourceLocation texture, int x, int y, float width, float height, float u, float v,
			float uWidth, float vHeight) {
		drawTexture(texture, x, y, width, height, u, v, uWidth, vHeight, Color.WHITE);
	}

	public static void drawTexture(ResourceLocation texture, float x, float y, float width, float height, float u,
			float v, float uWidth, float vHeight, Color color) {
		float a = color.a / 255.0F;
		float r = color.r / 255.0F;
		float g = color.g / 255.0F;
		float b = color.b / 255.0F;
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

	public static void drawBorder(int x, int y, int width, int height, int lineWidth, Color color) {
		drawRect(x, y, width, lineWidth, color);
		drawRect(x, y + height - lineWidth, width, lineWidth, color);
		drawRect(x, y + lineWidth, lineWidth, height - lineWidth, color);
		drawRect(x + width - lineWidth, y + lineWidth, lineWidth, height - lineWidth, color);
	}

	public static void drawDottedBorder(int x, int y, int width, int height, Color color) {
		boolean flag = drawDottedLine(x, y, width, true, color, true);
		flag = drawDottedLine(x + width, y, height, false, color, flag);
		flag = drawDottedLine(x + width, y + height, -width, true, color, flag);
		flag = drawDottedLine(x, y + height, -height, false, color, flag);
	}

	public static boolean drawDottedLine(int x, int y, int length, boolean isRow, Color color, boolean flag) {
		boolean flag2 = true;
		if (length < 0) {
			length = -length;
			flag2 = false;
		}
		for (; length > 0; length--) {
			if (flag) {
				drawRect(x, y, 1, 1, color);
			}
			if (isRow) {
				x += flag2 ? 1 : -1;
			} else {
				y += flag2 ? 1 : -1;
			}
			flag = !flag;
		}
		return flag;
	}

	public static void drawLine(int x, int y, int x2, int y2, Color color) {
		drawLine(x, y, 0, x2, y2, 0, color);
	}

	public static void drawLine(int x, int y, int z, int x2, int y2, int z2, Color color) {
		float a = color.a / 255.0F;
		float r = color.r / 255.0F;
		float g = color.g / 255.0F;
		float b = color.b / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(r, g, b, a);
		bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(x, y, z).endVertex();
		bufferbuilder.pos(x2, y2, z2).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
}
