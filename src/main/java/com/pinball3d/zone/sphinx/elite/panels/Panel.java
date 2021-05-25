package com.pinball3d.zone.sphinx.elite.panels;

import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.PanelGroup;

public class Panel {
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private String name;

	public Panel(EliteMainwindow parent, PanelGroup parentGroup, String name) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.name = name;
	}

	public void doRenderPre(int mouseX, int mouseY) {

	}

	public void doRender(int mouseX, int mouseY) {

	}

	public void doRenderPost(int mouseX, int mouseY) {

	}

	public void setParentGroup(PanelGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public PanelGroup getParentGroup() {
		return parentGroup;
	}

	public String getName() {
		return name;
	}

	public EliteMainwindow getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return name;
	}
}
