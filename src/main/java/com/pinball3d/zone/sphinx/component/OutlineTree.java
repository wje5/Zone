package com.pinball3d.zone.sphinx.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.sphinx.subscreen.SubscreenSynodLibrary;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;

public class OutlineTree extends Component {
	private PDDocument doc;
	private PDDocumentOutline outline;
	public int scrollingDistance;
	private List<Integer> indents;
	private List<PDOutlineItem> outlineList;

	public OutlineTree(IHasComponents parent, int x, int y, int height) {
		super(parent, x, y, 56, height);
	}

	public void setData(PDDocument data) {
		doc = data;
		outline = doc.getDocumentCatalog().getDocumentOutline();
		dimensionalityReduction();
	}

	@Override
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);
		if (outline == null) {
			return;
		}
		Iterator<PDOutlineItem> it = outlineList.iterator();
		FontRenderer fr = Util.getFontRenderer();
		int index = 0;
		while (it.hasNext()) {
			PDOutlineItem e = it.next();
			int posY = index * 15 - scrollingDistance;
			int cutUp = posY < 0 ? -posY : 0;
			int cutDown = posY + 13 - height > 0 ? posY + 13 - height : 0;
			if (cutUp < 13 && cutDown < 13) {
				Util.drawTexture(ICONS, indents.get(index) * 15, posY + cutUp - 3, 0, 187 + cutUp * 4,
						244 - indents.get(index) * 60, 69 - cutUp * 4 - cutDown * 4, 0.25F);
				if (e.hasChildren()) {
					Util.drawTexture(ICONS, indents.get(index) * 15 + 10, posY + cutUp + 3, e.isNodeOpen() ? 64 : 32,
							155 + cutUp * 4, 32, 32 - cutUp * 4 - cutDown * 4, 0.25F);
				} else {
					Util.drawTexture(ICONS, indents.get(index) * 15 + 12, posY + cutUp + 3, 96, 155 + cutUp * 4, 21,
							32 - cutUp * 4 - cutDown * 4, 0.25F);
				}
				if (cutUp <= 3 && cutDown <= 3 && indents.get(index) < 2) {
					String text = Util.formatString(e.getTitle());
					text = Util.formatStringToWidth(fr, text, 28 - indents.get(index) * 15);
					fr.drawString(text, 20 + indents.get(index) * 15, 3 + posY, 0xFF1ECCDE);
				}
			}
			index++;
		}
	}

	public void dimensionalityReduction() {
		if (outline == null) {
			return;
		}
		outlineList = new ArrayList<PDOutlineItem>();
		indents = new ArrayList<Integer>();
		outline.children().forEach(e -> {
			dimensionalityReduction(e, 0);
		});
		int maxScrollingDistance = outlineList == null ? 0 : outlineList.size() * 15 - height;
		if (maxScrollingDistance < 0) {
			scrollingDistance = 0;
			return;
		}
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
	}

	public void dimensionalityReduction(PDOutlineItem item, int indent) {
		outlineList.add(item);
		indents.add(indent);
		if (item.isNodeOpen()) {
			item.children().forEach(e -> {
				dimensionalityReduction(e, indent + 1);
			});
		}
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		int index = 0;
		Iterator<PDOutlineItem> it = outlineList.iterator();
		y += scrollingDistance;
		while (it.hasNext()) {
			PDOutlineItem e = it.next();
			if (y >= index * 15 && y <= index * 15 + 13 && x > indents.get(index) * 15) {
				if (e.hasChildren() && x >= indents.get(index) * 15 + 10 && x <= indents.get(index) * 15 + 18) {
					if (e.isNodeOpen()) {
						e.closeNode();
					} else {
						e.openNode();
					}
					dimensionalityReduction();
				} else {
					try {
						PDPage page = e.findDestinationPage(doc);
						Iterator<PDPage> it2 = doc.getPages().iterator();
						int i = 0;
						while (it2.hasNext()) {
							if (it2.next().equals(page)) {
								((SubscreenSynodLibrary) parent).setPage(i);
							}
							i++;
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				return true;
			}
			index++;
		}
		return false;
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		int maxScrollingDistance = outlineList == null ? 0 : outlineList.size() * 15 - height;
		if (maxScrollingDistance < 0) {
			scrollingDistance = 0;
			return true;
		}
		scrollingDistance += isUp ? 15 : -15;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		int maxScrollingDistance = outlineList == null ? 0 : outlineList.size() * 15 - height;
		if (maxScrollingDistance < 0) {
			scrollingDistance = 0;
			return true;
		}
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}
}
