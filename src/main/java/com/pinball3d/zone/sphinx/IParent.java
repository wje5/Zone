package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.FontRenderer;

public interface IParent {
	public int getWidth();

	public int getHeight();

	public int getXOffset();

	public int getYOffset();

	public FontRenderer getFontRenderer();

	public void putScreen(Subscreen screen);

	public void quitScreen(Subscreen screen);
}
