package com.pinball3d.zone.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.RenderDestination;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PDF {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final ResourceLocation location;
	protected PDDocument doc;
	protected PDFRenderer renderer;
	volatile protected List<PDFImage> images = new ArrayList<PDFImage>();

	public PDF(ResourceLocation loc) {
		location = loc;
	}

	public void load(IResourceManager resourceManager) throws IOException {
		if (location == PDFHelper.RESOURCE_LOCATION_EMPTY) {
			doc = new PDDocument();
		}
		IResource ir = null;
		try {
			ir = resourceManager.getResource(location);
			doc = PDFHelper.loadPdfFromStream(ir.getInputStream());
		} finally {
			IOUtils.closeQuietly(ir);
		}
	}

	public List<PDFImage> getImages() {
		if (renderer == null) {
			renderer = new PDFRenderer(doc);
		}
		if (images.isEmpty()) {
			for (int i = 0; i < doc.getNumberOfPages(); i++) {
				images.add(null);
			}
			new Thread() {
				@Override
				public void run() {
					for (int i = 0; i < doc.getNumberOfPages(); i++) {
						try {
							BufferedImage image = renderer.renderImage(i, 1, ImageType.RGB, RenderDestination.VIEW);
							images.set(i, new PDFImage(image, image.getWidth(), image.getHeight()));
							LOGGER.info(i);
						} catch (Throwable e) {
							LOGGER.error("Rendering PDF:");
							LOGGER.error(e);
							LOGGER.error("PDF being rendered");
							LOGGER.error("Resource location:" + location);
						}
					}
				}
			}.start();
		}
		return images;
	}
}