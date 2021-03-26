package com.pinball3d.zone.gui;

import java.util.Stack;

public interface IHasSubscreen {
	public void putScreen(Subscreen screen);

	public void removeScreen(Subscreen screen);

	public Stack<Subscreen> getSubscreens();
}
