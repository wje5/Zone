package com.pinball3d.zone.sphinx.elite.panels;

import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;
import com.pinball3d.zone.sphinx.elite.PanelGroup;

public class Panel {
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private FormattedString name;

	public Panel(EliteMainwindow parent, PanelGroup parentGroup, FormattedString name) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.name = name;
	}

	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {

	}

	public void doRender(int mouseX, int mouseY, float partialTicks) {

	}

	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {

	}

	public boolean onQuit() {
		return true;
	}

	public boolean keyTyped(char typedChar, int keyCode) {
		return false;
	}

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY) {

	}

	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return isMouseInPanel(mouseX, mouseY) ? Drag.EMPTY : null;
	}

	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		return;
	}

	public MouseType getMouseType(int mouseX, int mouseY) {
		return null;
	}

	public boolean isMouseInPanel(int mouseX, int mouseY) {
		return mouseX >= 0 && mouseX <= parentGroup.getPanelWidth() && mouseY >= 0
				&& mouseY <= parentGroup.getPanelHeight();
	}

	public void setParentGroup(PanelGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public PanelGroup getParentGroup() {
		return parentGroup;
	}

	public FormattedString getName() {
		return name;
	}

	public EliteMainwindow getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
