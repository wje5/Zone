package com.pinball3d.zone.sphinx.elite;

public class Panel {
	private EliteMainwindow parent;
	private PanelGroup parentGroup;
	private float x, y, width, height;
	private String name;

	public Panel(EliteMainwindow parent, PanelGroup parentGroup, String name) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.name = name;
	}

	public void doRenderPre(int mouseX, int mouseY) {

	}

	public void doRender(int mouseX, int mouseY) {
//		EliteRenderHelper.drawRect(0, 0, width, height, 0xFF4402D5);
	}

	public void doRenderPost(int mouseX, int mouseY) {

	}

	public void setParentGroup(PanelGroup parentGroup) {
		this.parentGroup = parentGroup;
	}

	public PanelGroup getParentGroup() {
		return parentGroup;
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}
}
