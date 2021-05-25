package com.pinball3d.zone.sphinx.elite;

public class ButtomBar {
	private EliteMainwindow parent;

	public ButtomBar(EliteMainwindow parent) {
		this.parent = parent;
	}

	public void doRender(int mouseX, int mouseY) {
		EliteRenderHelper.drawRect(0, parent.getHeight() - 21, parent.getWidth(), 21, 0xFF4A4A4A);
		FontHandler.renderText(20, parent.getHeight() - 19, "我能吞下玻璃而不伤身体(W)", 0xFFF0F0F0);
	}
}
