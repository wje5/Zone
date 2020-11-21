package com.pinball3d.zone.sphinx;

import java.io.IOException;

import com.pinball3d.zone.pdf.PDF;
import com.pinball3d.zone.pdf.PDFHelper;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SubscreenSynodLibrary extends Subscreen {
	public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/ui_border.png");
	volatile private PDF pdf;
	volatile private boolean failed;
	private PDFRenderer pdfbox;
	private OutlineTree tree;

	public SubscreenSynodLibrary(IParent parent) {
		this(parent, parent.getWidth() / 2 - 188, parent.getHeight() / 2 - 100);
	}

	public SubscreenSynodLibrary(IParent parent, int x, int y) {
		super(parent, x, y, 316, 200, true);
		new Thread() {
			@Override
			public void run() {
				pdf = PDFHelper.instance.getPdf(new ResourceLocation("zone:pdf/test2.pdf"));
				if (pdf == null) {
					failed = true;
				} else {
					pdfbox.setPDF(pdf);
					tree.setData(pdf.doc);
				}
			};
		}.start();
		components.add(pdfbox = new PDFRenderer(this, x + 76, y + 24, 224, 170));
		components.add(tree = new OutlineTree(this, x, y + 9, 195));
	}

	public void setPage(int page) {
		pdfbox.setPage(page);
	}

	@Override
	public void close() {
		try {
			if (pdf == null) {
				if (!failed) {
					new Thread() {
						@Override
						public void run() {
							while (pdf == null && !failed) {
								try {
									sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							try {
								pdf.doc.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						};
					}.start();
				}
			} else {
				pdf.doc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(TEXTURE, x + 60, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 276, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 60, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 276, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 100, y, x + 276, y + 40, 0x2F000000);
		Gui.drawRect(x + 60, y + 40, x + 316, y + 160, 0x2F000000);
		Gui.drawRect(x + 100, y + 160, x + 276, y + 200, 0x2F000000);
		Gui.drawRect(x + 70, y + 20, x + 306, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 76, y + 24, x + 300, y + 194, 0x651CC3B5);
		parent.getFontRenderer().drawString(I18n.format("sphinx.synod_library"), x + 75, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 75, y + 23, 226, 172, 1, 0xFF1ECCDE);
	}
}
