package com.pinball3d.zone.sphinx.elite;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class EliteRenderHelper {
	private static FontCache fontCache;

	public static void init() {
//		fontCache = new FontCache();
		FontHelper.init();
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
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(x, (double) y + height, 0.0D).endVertex();
		bufferbuilder.pos((double) x + width, (double) y + height, 0.0D).endVertex();
		bufferbuilder.pos((double) x + width, y, 0.0D).endVertex();
		bufferbuilder.pos(x, y, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void renderText(float x, float y, String s, int color) {
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GlStateManager.color(r, g, b, a);
		FontHelper.renderChar(x, y, s.charAt(0));
//		fontCache.cacheChar(s.toCharArray(), 0, s.toCharArray().length);

//		FONT.createGlyphVector(new FontRenderContext(null, true, true), s);
//		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D graphics = image.createGraphics();
//		graphics.setColor(Color.WHITE);
//		graphics.fillRect(0, 0, 100, 100);
//		graphics.drawString(s, x, y);
//		int[] array = image.getRGB(0, 0, 100, 100, null, 0, 100);
//		IntBuffer buffer = BufferUtils.createIntBuffer(40000);
//		buffer.put(array);
//		buffer.position(0);
//		System.out.println(buffer.get());
//		buffer.position(0);
//		GL11.glDrawPixels(100, 100, GL11.GL_RGBA, GL11.GL_INT, buffer);
//		GlyphVector vector = fontCache.layoutGlyphVector(s.toCharArray(), 0, s.toCharArray().length);
	}
}
