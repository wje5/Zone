package com.pinball3d.zone.sphinx;

import java.util.Stack;

import com.pinball3d.zone.gui.Subscreen;

public interface IHasSubscreen {
	public void putScreen(Subscreen screen);

	public void removeScreen(Subscreen screen);

	public Stack<Subscreen> getSubscreens();
}
