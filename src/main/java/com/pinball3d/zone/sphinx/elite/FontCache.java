package com.pinball3d.zone.sphinx.elite;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class FontCache {
	private Font FONT;
	private BufferedImage stringImage;
	private Graphics2D stringGraphics;
	private BufferedImage glyphCacheImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
	private Graphics2D glyphCacheGraphics = glyphCacheImage.createGraphics();
	private FontRenderContext fontRenderContext = glyphCacheGraphics.getFontRenderContext();
	private IntBuffer singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
	private IntBuffer imageBuffer = ByteBuffer.allocateDirect(4 * 512 * 512).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
	private int imageData[] = new int[512 * 512];
	private int textureName;

	public FontCache() {
		try {
			IResource res = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("zone:fonts/sphinx/elite/droidsans.ttf"));
			FONT = Font.createFont(Font.TRUETYPE_FONT, res.getInputStream());
		} catch (IOException | FontFormatException e) {
			Zone.logger.error("EliteRenderHelper has throw an exception when loading font.", e);
		}
		glyphCacheGraphics.setBackground(new Color(255, 255, 255, 0));

		glyphCacheGraphics.setComposite(AlphaComposite.Src);

		allocateGlyphCacheTexture();
		allocateStringImage(128, 512);
		GraphicsEnvironment.getLocalGraphicsEnvironment().preferLocaleFonts();
	}

	public GlyphVector layoutGlyphVector(char text[], int start, int limit) {
		return FONT.layoutGlyphVector(fontRenderContext, text, start, limit, Font.LAYOUT_LEFT_TO_RIGHT);
	}

	public void cacheChar(char[] text, int start, int limit) {
		GlyphVector vector = layoutGlyphVector(text, start, limit);
		for (int i = 0; i < vector.getNumGlyphs(); i++) {
			Point2D pos = vector.getGlyphPosition(i);
			pos.setLocation(pos.getX() + 2 * i, pos.getY());
			vector.setGlyphPosition(i, pos);
		}
		Rectangle vectorBounds = vector.getPixelBounds(fontRenderContext, 0, 0);
		if (stringImage == null || vectorBounds.width > stringImage.getWidth()
				|| vectorBounds.height > stringImage.getHeight()) {
			int width = Math.max(vectorBounds.width, stringImage == null ? 512 : stringImage.getWidth());
			int height = Math.max(vectorBounds.height, stringImage == null ? 128 : stringImage.getHeight());
			allocateStringImage(width, height);
		}
		stringGraphics.clearRect(0, 0, vectorBounds.width, vectorBounds.height);
		stringGraphics.drawGlyphVector(vector, -vectorBounds.x, -vectorBounds.y);

		updateTexture(new Rectangle(0, 0, 100, 100));
		GlStateManager.bindTexture(textureName);
		Util.drawTexture(0, 0, 100, 100);
	}

	private void allocateStringImage(int width, int height) {
		stringImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		stringGraphics = stringImage.createGraphics();
		setRenderingHints();
		stringGraphics.setBackground(new Color(255, 255, 255, 0));
		stringGraphics.setPaint(Color.WHITE);
	}

	private void updateTexture(Rectangle dirty) {
		/* Only update OpenGL texture if changes were made to the texture */
		if (dirty != null) {
			/* Load imageBuffer with pixel data ready for transfer to OpenGL texture */
			updateImageBuffer(dirty.x, dirty.y, dirty.width, dirty.height);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);
			GlStateManager.bindTexture(textureName);
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, dirty.x, dirty.y, dirty.width, dirty.height, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, imageBuffer);
		}
	}

	private void allocateGlyphCacheTexture() {
		/* Initialize the background to all white but fully transparent. */
		glyphCacheGraphics.clearRect(0, 0, 512, 512);

		/* Allocate new OpenGL texure */
		((Buffer) singleIntBuffer).clear();
		GL11.glGenTextures(singleIntBuffer);
		textureName = singleIntBuffer.get(0);

		/* Load imageBuffer with pixel data ready for transfer to OpenGL texture */
		updateImageBuffer(0, 0, 512, 512);

		/*
		 * Initialize texture with the now cleared BufferedImage. Using a texture with
		 * GL_ALPHA8 internal format may result in faster rendering since the GPU has to
		 * only fetch 1 byte per texel instead of 4 with a regular RGBA texture.
		 */
		GlStateManager.bindTexture(textureName);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA8, 512, 512, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				imageBuffer);

		/*
		 * Explicitely disable mipmap support becuase updateTexture() will only update
		 * the base level 0
		 */
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}

	private void updateImageBuffer(int x, int y, int width, int height) {
		/*
		 * Copy raw pixel data from BufferedImage to imageData array with one integer
		 * per pixel in 0xAARRGGBB form
		 */
		glyphCacheImage.getRGB(x, y, width, height, imageData, 0, width);

		/* Swizzle each color integer from Java's ARGB format to OpenGL's RGBA */
		for (int i = 0; i < width * height; i++) {
			int color = imageData[i];
			imageData[i] = (color << 8) | (color >>> 24);
		}

		/*
		 * Copy int array to direct buffer; big-endian order ensures a 0xRR, 0xGG, 0xBB,
		 * 0xAA byte layout
		 */
		((Buffer) imageBuffer).clear();
		imageBuffer.put(imageData);
		((Buffer) imageBuffer).flip();
	}

	private void setRenderingHints() {
		stringGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		stringGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		stringGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
	}
}
