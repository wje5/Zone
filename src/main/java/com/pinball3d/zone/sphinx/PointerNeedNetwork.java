package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class PointerNeedNetwork extends Gui {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public WorldPos pos;

	public PointerNeedNetwork(WorldPos pos) {
		this.pos = pos;
	}

	public abstract void doRender(int offsetX, int offsetZ);
}
