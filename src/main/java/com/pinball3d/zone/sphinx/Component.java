package com.pinball3d.zone.sphinx;

public class Component {
	public int x, y;
	protected int width, height;
	protected IParent parent;

	public Component(IParent parent, int x, int y, int width, int height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void onClickScreen(int x, int y, boolean isLeft) {
		if (isLeft) {
			onLeftClick(x, y);
		} else {
			onRightClick(x, y);
		}
	}

	public void onLeftClick(int x, int y) {

	}

	public void onRightClick(int x, int y) {

	}

	public void onDrag(int moveX, int moveY) {

	}

	public void doRender(int mouseX, int mouseY) {

	}
}
