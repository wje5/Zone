package com.pinball3d.zone.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

public class PDFImage extends AbstractTexture {
	private final BufferedImage image;
	public final float width, height;

	public PDFImage(BufferedImage img, float width, float height) {
		image = img;
		this.width = width;
		this.height = height;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		this.deleteGlTexture();
		TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, false, false);
	}
}
