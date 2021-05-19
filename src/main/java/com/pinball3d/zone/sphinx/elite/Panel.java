package com.pinball3d.zone.sphinx.elite;

import com.pinball3d.zone.util.Util;

import net.minecraft.client.renderer.GlStateManager;

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
		String s = getName();
		GlStateManager.pushMatrix();
		long l = System.nanoTime();
		GlStateManager.scale(0.5F, 0.5F, 1.0F);
		Util.getFontRenderer().drawString(s, 10, 10, 0xFFA0A0A0);
		GlStateManager.popMatrix();
		l = System.nanoTime();
		float f = FontHandler.renderText(10, 20, s, 0xFFF0F0F0);
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
}
