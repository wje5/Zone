package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class PointerStorage extends Gui {
	public int x, z;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public PointerStorage(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, x - offsetX - 3, z - offsetZ - 3, 149, 0, 13, 13, 0.5F);
	}
}
