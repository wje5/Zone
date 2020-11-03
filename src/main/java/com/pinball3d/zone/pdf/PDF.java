package com.pinball3d.zone.pdf;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PDF {
	protected final ResourceLocation location;
	protected PDDocument doc;

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
}
