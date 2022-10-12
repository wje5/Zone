package com.pinball3d.zone.sphinx.elite;

import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;

public interface IDropDownList {
	public void doRender(int mouseX, int mouseY);

	public boolean onQuit();

	public void keyTyped(char typedChar, int keyCode);

	public void mouseMoved(int mouseX, int mouseY, int moveX, int moveY);

	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton);

	public boolean mouseReleased(int mouseX, int mouseY, int mouseButton);

	public boolean isMouseInList(int mouseX, int mouseY);

	public default MouseType getMouseType(int mouseX, int mouseY) {
		return null;
	}
}
