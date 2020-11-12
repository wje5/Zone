package com.pinball3d.zone.sphinx;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

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
		this(parent, parent.getWidth() / 2 - 128, parent.getHeight() / 2 - 100);
	}

	public SubscreenSynodLibrary(IParent parent, int x, int y) {
		super(parent, x, y, 256, 200, true);
		new Thread() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				pdf = PDFHelper.instance.getPdf(new ResourceLocation("zone:pdf/test.pdf"));
				long time2 = System.currentTimeMillis();
				System.out.println(time2 - time);
				if (pdf == null) {
					failed = true;
				} else {
					pdfbox.setPDF(pdf);
					PDDocumentOutline outline = pdf.doc.getDocumentCatalog().getDocumentOutline();
					outline.children().forEach(e -> {
						printOutline(e);
						System.out.println("#####");
					});
					tree.setData(outline);
				}
			};
		}.start();
		components.add(pdfbox = new PDFRenderer(this, x + 16, y + 24, 224, 170));
		components.add(tree = new OutlineTree(this, x - 28, y + 24, 42, 170));
	}

	private static void printOutline(PDOutlineItem item) {
		System.out.println(item.getTitle());
		if (item.hasChildren()) {
			System.out.println("{");
			item.children().forEach(e -> {
				printOutline(e);
			});
			System.out.println("}");
		}
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
		Util.drawTexture(TEXTURE, x, y, 0, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 216, y, 80, 0, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x, y + 160, 0, 80, 80, 80, 0.5F);
		Util.drawTexture(TEXTURE, x + 216, y + 160, 80, 80, 80, 80, 0.5F);
		Gui.drawRect(x + 40, y, x + 216, y + 40, 0x2F000000);
		Gui.drawRect(x, y + 40, x + 256, y + 160, 0x2F000000);
		Gui.drawRect(x + 40, y + 160, x + 216, y + 200, 0x2F000000);
		Gui.drawRect(x + 10, y + 20, x + 246, y + 22, 0xFF20E6EF);
		Gui.drawRect(x + 16, y + 24, x + 240, y + 194, 0x651CC3B5);
		parent.getFontRenderer().drawString(I18n.format("sphinx.synod_library"), x + 15, y + 8, 0xFF1ECCDE);
		Util.drawBorder(x + 15, y + 23, 226, 172, 1, 0xFF1ECCDE);
//		Util.drawBorder(x + 15, y + 23, 270, 172, 1, 0xFF1ECCDE);
	}
}
