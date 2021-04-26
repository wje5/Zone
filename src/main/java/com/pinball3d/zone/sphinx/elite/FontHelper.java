package com.pinball3d.zone.sphinx.elite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.Zone;
import com.pvporbit.freetype.Bitmap;
import com.pvporbit.freetype.Face;
import com.pvporbit.freetype.FreeType;
import com.pvporbit.freetype.FreeTypeConstants;
import com.pvporbit.freetype.GlyphSlot;
import com.pvporbit.freetype.Library;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class FontHelper {
	private static ResourceLocation FONT_LOCATION = new ResourceLocation("zone:fonts/sphinx/elite/droidsans.ttf");
	private static Library library;
	private static Face face;

	public static void init() {
		library = FreeType.newLibrary();
		if (library == null) {
			throw new RuntimeException("Error initializing FreeType.");
		}
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

	public static void renderChar(float x, float y, char c) {
		long nano = System.nanoTime();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		face.selectSize(1);
		face.setPixelSizes(0, 28);
		face.loadChar(c, FreeTypeConstants.FT_LOAD_RENDER);
		GlyphSlot gss = face.getGlyphSlot();
		Bitmap bmp = gss.getBitmap();
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glRasterPos2i(72, 36);
		ByteBuffer buf = bmp.getBuffer();
//		int d = bmp.getWidth() / 8 + (bmp.getWidth() % 8 > 0 ? 1 : 0);
//		byte[] bytes = new byte[d * bmp.getRows()];
//		int k = bytes.length - d, m = 0;
//		byte t = 0;
//		for (int i = 0; i < bmp.getRows(); i++) {
//			for (int j = 0; j < bmp.getWidth(); j++) {
//				int f = buf.get() != -1 ? 0 : 1;
//				t |= f << (7 - m);
//				if (m == 7) {
//					bytes[k++] = t;
//					t = 0;
//					m = 0;
//				} else {
//					m++;
//				}
//			}
//			if (m != 0) {
//				bytes[k++] = t;
//			}
//			t = 0;
//			m = 0;
//			k -= d * 2;
//		}
//		buf = BufferUtils.createByteBuffer(bytes.length);
//		buf.put(bytes);
//		buf.position(0);
//		GL11.glBitmap(bmp.getWidth(), bmp.getRows(), 0, 0, 0, 0, buf);
		GL11.glDrawPixels(bmp.getWidth(), bmp.getRows(), GL11.GL_LUMINANCE, GL11.GL_UNSIGNED_BYTE, buf);
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
		System.out.println(System.nanoTime() - nano);
	}
}
