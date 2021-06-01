package com.pinball3d.zone.sphinx.elite;

public interface IDropDownList {
	public void doRender(int mouseX, int mouseY);

	public boolean onQuit();

	public void keyTyped(char typedChar, int keyCode);

	public void onMouseMoved(int mouseX, int mouseY, int moveX, int moveY);

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton);

	public boolean mouseReleased(int mouseX, int mouseY, int mouseButton);

	public boolean isMouseInList(int mouseX, int mouseY);
}
