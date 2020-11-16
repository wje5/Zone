package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.google.common.collect.Streams;

import net.minecraft.client.gui.FontRenderer;

public class OutlineTree extends Component {
	private PDDocument doc;
	private PDDocumentOutline outline;
	public int scrollingDistance, maxScrollingDistance;

	public OutlineTree(IParent parent, int x, int y, int height) {
		super(parent, x, y, 56, height);
	}

	public void setData(PDDocument data) {
		doc = data;
		outline = doc.getDocumentCatalog().getDocumentOutline();
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (doc == null) {
			return;
		}
		computeMaxScrollingDistance();
		Iterator<PDOutlineItem> it = outline.children().iterator();
		FontRenderer fr = parent.getFontRenderer();
		int yOffset = 0;
		while (it.hasNext()) {
			PDOutlineItem e = it.next();
			int posY = yOffset - scrollingDistance;
			int cutUp = posY < 0 ? -posY : 0;
			int cutDown = posY + 13 - height > 0 ? posY + 13 - height : 0;
			if (cutUp < 13 && cutDown < 13) {
				Util.drawTexture(ICONS, x, y + posY + cutUp, 0, 187 + cutUp * 4, 225, 50 - cutUp * 4 - cutDown * 4,
						0.25F);
				if (cutUp <= 3 && cutDown <= 3) {
					String text = Util.formatString(e.getTitle());
					text = fr.listFormattedStringToWidth(text, 43).get(0);
					fr.drawString(text, x + 3, y + 3 + posY, 0xFF1ECCDE);
				}
			}
			yOffset += 15;
		}
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		int yOffset = 0;
		Iterator<PDOutlineItem> it = outline.children().iterator();
		y += scrollingDistance;
		while (it.hasNext()) {
			PDOutlineItem e = it.next();
			if (y >= yOffset && y <= yOffset + 13) {
				System.out.println(e.getTitle());
				try {
					PDPage page = e.findDestinationPage(doc);
					Iterator<PDPage> it2 = doc.getPages().iterator();
					int i = 0;
					while (it2.hasNext()) {
						if (it2.next().equals(page)) {
							System.out.println(i);
							((SubscreenSynodLibrary) parent).setPage(i);
						}
						i++;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			yOffset += 15;
		}
		return false;
	}

	private void computeMaxScrollingDistance() {
//		int count = 0;
//		Iterator<PDOutlineItem> it = outline.children().iterator();
//		while (it.hasNext()) {
//			it.next();
//			count++;
//		}
		maxScrollingDistance = outline == null ? 0
				: (int) (Streams.stream(outline.children()).count() * 15 - 15 - height);
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		super.onMouseScroll(mouseX, mouseY, isUp);
		computeMaxScrollingDistance();
		scrollingDistance += isUp ? 15 : -15;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		super.onDrag(mouseX, mouseY, moveX, moveY);
		computeMaxScrollingDistance();
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}
}
