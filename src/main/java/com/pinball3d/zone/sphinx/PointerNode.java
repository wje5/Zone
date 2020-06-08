package com.pinball3d.zone.sphinx;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class PointerNode extends Gui {
	public int x, z;
	public boolean valid = true;
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public PointerNode(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void doRender(int offsetX, int offsetZ) {
		if (valid) {
			Util.drawTexture(TEXTURE, x - offsetX - 4, z - offsetZ - 4, 136, 0, 16, 16, 0.5F);
		}
	}
}
