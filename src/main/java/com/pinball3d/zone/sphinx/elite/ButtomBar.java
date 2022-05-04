package com.pinball3d.zone.sphinx.elite;

public class ButtomBar {
	private EliteMainwindow parent;

	public ButtomBar(EliteMainwindow parent) {
		this.parent = parent;
	}

	public void doRender(int mouseX, int mouseY) {
		EliteRenderHelper.drawRect(0, parent.getHeight() - 21, parent.getWidth(), 21, Color.COMP_BG_MEDIUM_DARK);
//		FontHandler.renderText(20, parent.getHeight() - 19, new FormattedString("我能吞下玻璃而不伤身体(W)"), Color.TEXT_LIGHT);
		FontHandler.renderText(20, parent.getHeight() - 19, new FormattedString("1 个对象"), Color.TEXT_LIGHT);
	}
}
