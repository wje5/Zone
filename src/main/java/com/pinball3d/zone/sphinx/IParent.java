package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.tileentity.INeedNetwork;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public interface IParent {
	public int getWidth();

	public int getHeight();

	public int getXOffset();

	public int getYOffset();

	public FontRenderer getFontRenderer();

	public void putScreen(Subscreen screen);

	public void quitScreen(Subscreen screen);

	default public ItemStack getTerminal() {
		return ItemStack.EMPTY;
	}

	default public INeedNetwork getNeedNetworkTileEntity() {
		return null;
	}
}
