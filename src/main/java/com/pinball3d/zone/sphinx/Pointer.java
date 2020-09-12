package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class Pointer extends Gui {
	public int x, z;
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public Pointer(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public abstract void doRender(int offsetX, int offsetZ);
}
