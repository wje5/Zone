package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.pdf.PDF;
import com.pinball3d.zone.pdf.PDFImage;
import com.pinball3d.zone.util.Util;

public class PDFRenderer extends Component {
	volatile private PDF pdf;
	private int scrollingDistance;
	private float maxWidth, totalHeight, scale;
	private int maxScrollingDistance;

	public PDFRenderer(IHasComponents parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	public void setPDF(PDF pdf) {
		this.pdf = pdf;
		for (float f : pdf.widths) {
			maxWidth = f > maxWidth ? f : maxWidth;
		}
		for (float f : pdf.heights) {
			totalHeight += f;
		}
		scale = width / maxWidth;
		maxScrollingDistance = (int) (totalHeight * scale) - height;
	}

	public void setPage(int page) {
		float f = 0;
		if (pdf != null) {
			int max = pdf.doc.getNumberOfPages();
			for (int i = 0; i < max; i++) {
				if (page > i) {
					f += pdf.heights[i] * scale;
				}
			}
			scrollingDistance = (int) f;
		}
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, boolean isUp) {
		if (super.onMouseScroll(mouseX, mouseY, isUp)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
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
		scrollingDistance -= moveY;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > maxScrollingDistance ? maxScrollingDistance : scrollingDistance;
		return true;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (enable != null && !enable.getAsBoolean()) {
			return;
		}
		float yOffset = 0;
		if (pdf != null) {
			int max = pdf.doc.getNumberOfPages();
			for (int i = 0; i < max; i++) {
				float imageHeight = pdf.heights[i] * scale;
				if (yOffset + imageHeight >= scrollingDistance && yOffset <= scrollingDistance + height) {
					PDFImage image = pdf.getImage(i);
					int yOff = (int) (scrollingDistance - yOffset);
					Util.drawPDF(image, getX(), yOff < 0 ? getY() - yOff : getY(), width, yOff < 0 ? 0 : yOff,
							(int) (yOff < 0 ? height + yOff
									: imageHeight - yOff > height ? height : imageHeight - yOff));
				}
				yOffset += imageHeight;
			}
		}
	}
}
