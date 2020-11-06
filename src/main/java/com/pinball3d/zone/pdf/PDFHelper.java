package com.pinball3d.zone.pdf;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

public class PDFHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
	public static final PDF PDF_EMPTY = new PDF(RESOURCE_LOCATION_EMPTY);
	public final IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
	public static PDFHelper instance = new PDFHelper();

	public static PDDocument loadPdfFromStream(InputStream stream) throws IOException {
		PDDocument pdf;
		try {
			pdf = PDDocument.load(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return pdf;
	}

	public PDF getPdf(ResourceLocation loc) {
		PDF pdf = new PDF(loc);
		return loadPdf(pdf) ? pdf : null;
	}

	public boolean loadPdf(PDF pdf) {
		boolean flag = true;
		try {
			pdf.load(manager);
		} catch (IOException e) {
			if (pdf.location != RESOURCE_LOCATION_EMPTY) {
				LOGGER.warn("Failed to load pdf: {}", pdf.location, e);
			}
			flag = false;
		} catch (Throwable throwable) {
			final PDF f = pdf;
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering PDF");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
			crashreportcategory.addCrashSection("Resource location",
					pdf != null ? pdf.location : RESOURCE_LOCATION_EMPTY);
			crashreportcategory.addDetail("PDF object class", new ICrashReportDetail<String>() {
				@Override
				public String call() throws Exception {
					return f.getClass().getName();
				}
			});
			throw new ReportedException(crashreport);
		}
		return flag;
	}

	public void loadPdfImage(PDFImage image) {
		try {
			image.loadTexture(manager);
		} catch (Throwable e) {
			CrashReport crashreport = CrashReport.makeCrashReport(e, "Rendering PDF Image");
			throw new ReportedException(crashreport);
		}
	}
}
