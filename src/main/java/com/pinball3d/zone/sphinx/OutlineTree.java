package com.pinball3d.zone.sphinx;

import java.io.IOException;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import net.minecraft.client.gui.FontRenderer;

public class OutlineTree extends Component {
	PDDocumentOutline outline;

	public OutlineTree(IParent parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	public void setData(PDDocumentOutline data) {
		outline = data;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (outline == null) {
			return;
		}
		Iterator<PDOutlineItem> it = outline.children().iterator();
		FontRenderer fr = parent.getFontRenderer();
		int yOffset = 0;
		while (it.hasNext()) {
			PDOutlineItem e = it.next();
			try {
				String text = Util.formatString(e.getTitle());
				fr.drawSplitString(text, x, y + yOffset, width, e.getTextColor().toRGB());
				yOffset += fr.getWordWrappedHeight(text, width) + 3;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
