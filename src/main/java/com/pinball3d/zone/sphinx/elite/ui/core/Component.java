package com.pinball3d.zone.sphinx.elite.ui.core;

import com.pinball3d.zone.math.Box4i;
import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;

public class Component {
	protected EliteMainwindow parent;
	protected Subpanel parentPanel;
	private int width, height, renderWidth;

	public Component(EliteMainwindow parent, Subpanel parentPanel, int width, int height) {
		this.parent = parent;
		this.parentPanel = parentPanel;
		this.width = width;
		this.height = height;
	}

	public void refresh() {

	}

	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {

	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {

	}

	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {
		EliteRenderHelper.drawBorder(0, 0, getRenderWidth(), getHeight(), 1, Color.RED);
	}

	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return null;
	}

	public boolean onMouseScrolled(int mouseX, int mouseY, int distance) {
		return false;
	}

	public MouseType getMouseType(int mouseX, int mouseY) {
		return null;
	}

	public int getWidth() {
		return width;
	}

	public int getMinWidth() {
		return getWidth();
	}

	public int getRenderWidth() {
		return renderWidth;
	}

	public void setRenderWidth(int renderWidth) {
		this.renderWidth = renderWidth;
	}

	public int getHeight() {
		return height;
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Pos2i getPos() {
		return parentPanel == null ? new Pos2i(0, 0)
				: parentPanel.getPos().add(parentPanel.components.get(this)).add(0,
						-parentPanel.getScrollingDistance());
	}

	public final Box4i getBox() {
		Pos2i pos = getPos();
		return new Box4i(pos.x, pos.y, getRenderWidth(), getHeight());
	}

	public final Box4i getRenderBox() {
		return parentPanel == null ? getBox() : parentPanel.getRenderBox().intersection(getBox());
	}
}
