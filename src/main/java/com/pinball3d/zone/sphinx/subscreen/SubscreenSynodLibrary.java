package com.pinball3d.zone.sphinx.subscreen;

import java.io.IOException;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.pdf.PDF;
import com.pinball3d.zone.pdf.PDFHelper;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.OutlineTree;
import com.pinball3d.zone.sphinx.component.PDFRenderer;
import com.pinball3d.zone.util.Util;

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

	public SubscreenSynodLibrary(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 188, getDisplayHeight() / 2 - 100);
	}

	public SubscreenSynodLibrary(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 316, 200, true);
		new Thread() {
			@Override
			public void run() {
				pdf = PDFHelper.instance.getPdf(new ResourceLocation("zone:pdf/sphinx_manual.pdf"));
				if (pdf == null) {
					failed = true;
				} else {
					pdfbox.setPDF(pdf);
					tree.setData(pdf.doc);
				}
			};
		}.start();
		addComponent(pdfbox = new PDFRenderer(this, x + 76, y + 24, 224, 170));
		addComponent(tree = new OutlineTree(this, x, y + 9, 195));
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
		Util.drawTexture(TEXTURE, x + 55, y - 5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 271, y - 5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 55, y + 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(TEXTURE, x + 271, y + 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(x + 104, y, x + 271, y + 44, 0x2F000000);
		Gui.drawRect(x + 60, y + 44, x + 316, y + 155, 0x2F000000);
		Gui.drawRect(x + 104, y + 155, x + 271, y + 200, 0x2F000000);
		Util.renderGlowHorizonLine(x + 70, y + 20, 236);
		Gui.drawRect(x + 76, y + 24, x + 300, y + 194, 0x651CC3B5);
		Util.getFontRenderer().drawString(I18n.format("sphinx.synod_library"), x + 75, y + 8, 0xFF1ECCDE);
		Util.renderGlowBorder(x + 75, y + 23, 226, 172);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}
