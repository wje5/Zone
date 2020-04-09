package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class PointerLiving extends Gui {
	public int x, z;
	public boolean isMob;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public PointerLiving(int x, int z, boolean isMob) {
		this.x = x;
		this.z = z;
		this.isMob = isMob;
	}

	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, x - offsetX - 4, z - offsetZ - 4, isMob ? 0 : 16, 0, 16, 16, 0.5F);
	}
}