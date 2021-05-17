package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mlomb.freetypejni.Bitmap;
import com.mlomb.freetypejni.Face;
import com.mlomb.freetypejni.FreeType;
import com.mlomb.freetypejni.FreeTypeConstants;
import com.mlomb.freetypejni.GlyphMetrics;
import com.mlomb.freetypejni.GlyphSlot;
import com.mlomb.freetypejni.Library;
import com.pinball3d.zone.Zone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class FontHelper {
	private static ResourceLocation FONT_LOCATION = new ResourceLocation("zone:fonts/droidsans.ttf");
	private static Library library;
	private static Face face;
	private static Map<Character, Float> widths = new HashMap<Character, Float>();
	public static final int SIZE = 12;

	public static void init() {
		System.load("D:/work/Zone/natives/freetype_64.dll");
//		NativeHandler.loadNative("/freetype");
		library = FreeType.newLibrary();
		if (library == null) {
			throw new RuntimeException("Error initializing FreeType.");
		}
		Zone.logger.error(library.getVersion());
		try {
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(FONT_LOCATION);
			InputStream stream = res.getInputStream();
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			face = library.newFace(bytes, 0);
		} catch (IOException e) {
			Zone.logger.error("Error creating face from file '" + FONT_LOCATION.toString() + "'.");
			throw new RuntimeException(e);
		}
	}

	private static float renderChar(float x, float y, char c, int color) {
		face.setPixelSizes(0, SIZE);
		face.loadChar(c, FreeTypeConstants.FT_LOAD_RENDER);
		GlyphSlot gss = face.getGlyphSlot();
		Bitmap bmp = gss.getBitmap();
		ByteBuffer buf = bmp.getBuffer();
		int width = bmp.getWidth();
		int height = bmp.getRows();
		double s = 0.25F;
		GlyphMetrics metric = gss.getMetrics();
		float yOffset = metric.getHoriBearingY() * 1.0F / metric.getHeight() * height;
		y -= yOffset * s - 3;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		int a = (color >> 24 & 255);
		int r = (color >> 16 & 255);
		int g = (color >> 8 & 255);
		int b = (color & 255);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				short t = (short) (buf.get() & 0xFF);
				short alpha = (short) ((t / 255F) * a);
				bufferbuilder.pos(x + j * s, y + i * s + s, 0).color(r, g, b, alpha).endVertex();
				bufferbuilder.pos(x + j * s + s, y + i * s + s, 0).color(r, g, b, alpha).endVertex();
				bufferbuilder.pos(x + j * s + s, y + i * s, 0).color(r, g, b, alpha).endVertex();
				bufferbuilder.pos(x + j * s, y + i * s, 0).color(r, g, b, alpha).endVertex();
			}
		}
		tessellator.draw();
		return width * 0.25F;
	}

	public static float renderText(float x, float y, String s, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		float t = 0;
		boolean underLine = false;
		char[] a = s.toCharArray();
		for (int i = 0; i < a.length; i++) {
			char c = a[i];
			if (c == '§') {
				if (a.length > i + 1) {
					char d = a[i + 1];
					if (d == 'n') {
						underLine = true;
						i++;
						continue;
					} else if (d == 'r') {
						underLine = false;
						i++;
						continue;
					}
				}
			}

			float w = renderChar(t + x, y, c, color);
			if (underLine) {
				EliteRenderHelper.drawRect(t + x - 0.25F, y + SIZE * 0.25F + 0.25F, w + 0.25F, 0.25F, color);
				GlStateManager.enableBlend();
				GlStateManager.disableTexture2D();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ZERO);
			}
			t += w + 0.25F;
		}
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		return t;
	}

	public static float getStringWidth(String s) {
		if (s == null || s.isEmpty()) {
			return 0;
		}
		float w = 0;
		char[] a = s.toCharArray();
		for (int i = 0; i < a.length; i++) {
			char c = a[i];
			if (c == '§') {
				if (a.length > i + 1) {
					char d = a[i + 1];
					if (d == 'n') {
						i++;
						continue;
					} else if (d == 'r') {
						i++;
						continue;
					}
				}
			}
			w += getCharWidth(c) + 0.25F;
		}
		return w;
	}

	public static float getCharWidth(char c) {
		if (widths.containsKey(c)) {
			return widths.get(c);
		}
		face.setPixelSizes(0, 12);
		face.loadChar(c, FreeTypeConstants.FT_LOAD_RENDER);
		float w = face.getGlyphSlot().getBitmap().getWidth() * 0.25F;
		widths.put(c, w);
		return w;
	}
}
