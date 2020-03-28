package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class PointerPlayer extends Gui {
	public int x, z;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public PointerPlayer(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, x - offsetX - 4, z - offsetZ - 12, 32, 0, 16, 24, 0.5F);
	}
}
