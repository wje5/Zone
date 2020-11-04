package com.pinball3d.zone.sphinx;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinball3d.zone.pdf.PDF;
import com.pinball3d.zone.pdf.PDFHelper;
import com.pinball3d.zone.pdf.PDFImage;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenSynodLibrary extends Subscreen {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	List<PDFImage> images;

	public SubscreenSynodLibrary(IParent parent) {
		this(parent, parent.getWidth() / 2 - 150, parent.getHeight() / 2 - 100);
	}

	public SubscreenSynodLibrary(IParent parent, int x, int y) {
		super(parent, x, y, 300, 200, true);
		long time = System.currentTimeMillis();
		PDF pdf = PDFHelper.instance.getPdf(new ResourceLocation("zone:pdf/test.pdf"));
		long time2 = System.currentTimeMillis();
		System.out.println(time2 - time);
		images = pdf.getImages();
		long time3 = System.currentTimeMillis();
		System.out.println(time3 - time2);
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 260, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 40, y, x + 260, y + 40, 0x2F000000);
		Gui.drawRect(x, y + 40, x + 300, y + 160, 0x2F000000);
		Gui.drawRect(x + 40, y + 160, x + 260, y + 200, 0x2F000000);
		Gui.drawRect(x + 10, y + 20, x + 290, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 16, y + 24, x + 284, y + 194, 0x651CC3B5);
		parent.getFontRenderer().drawString(I18n.format("sphinx.synod_library"), x + 15, y + 8, 0xFF1ECCDE);
		PDFImage image = images.get(0);
		if (image != null) {
			Util.drawPDF(image, x + 91, y + 34, 183, 0, 150);
		}
		Util.drawBorder(x + 90, y + 33, 185, 152, 1, 0xFF1ECCDE);
		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
	}
}
