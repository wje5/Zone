package com.pinball3d.zone.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.RenderDestination;

import com.pinball3d.zone.Zone;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PDF {
	protected final ResourceLocation location;
	public PDDocument doc;
	protected PDFRenderer renderer;
	volatile protected List<PDFImage> images = new ArrayList<PDFImage>();
	volatile public float[] widths, heights;

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
		if (doc != null) {
			int max = doc.getNumberOfPages();
			widths = new float[max];
			heights = new float[max];
			for (int i = 0; i < max; i++) {
				images.add(null);
			}
			int i = 0;
			Iterator<PDPage> it = doc.getPages().iterator();
			while (it.hasNext()) {
				PDRectangle r = it.next().getCropBox();
				widths[i] = r.getWidth();
				heights[i] = r.getHeight();
				i++;
			}
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
						} catch (Throwable e) {
							Zone.logger.error("Rendering PDF:");
							Zone.logger.error(e);
							Zone.logger.error("PDF being rendered");
							Zone.logger.error("Resource location:" + location);
						}
					}
				}
			}.start();
		}
		return images;
	}

	public PDFImage getImage(int page) {
		PDFImage image = images.get(page);
		if (renderer == null) {
			renderer = new PDFRenderer(doc);
		}
		if (image == null && doc != null && doc.getNumberOfPages() > page) {
			try {
				BufferedImage img = renderer.renderImage(page, 1, ImageType.RGB, RenderDestination.VIEW);
				image = new PDFImage(img, img.getWidth(), img.getHeight());
			} catch (Throwable e) {
				Zone.logger.error("Rendering PDF:");
				e.printStackTrace();
				Zone.logger.error(e);
				Zone.logger.error("Resource location:" + location);
			}
		}
		if (image != null) {
			images.set(page, image);
		}
		for (int i = 0; i < images.size(); i++) {
			if (i < page - 2 || i > page + 2) {
				PDFImage e = images.get(i);
				if (e != null) {
					images.get(i).deleteGlTexture();
					images.set(i, null);
				}
			}
		}
		return image;
	}
}
