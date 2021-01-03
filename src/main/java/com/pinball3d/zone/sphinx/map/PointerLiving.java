package com.pinball3d.zone.sphinx.map;

import com.pinball3d.zone.util.Util;

public class PointerLiving extends Pointer {
	public boolean isMob;

	public PointerLiving(int x, int z, boolean isMob) {
		super(new BoundingBox(x - 4, z - 4, x + 4, z + 4));
		this.isMob = isMob;
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, box.x - offsetX, box.y - offsetZ, isMob ? 0 : 16, 0, 16, 16, 0.5F);
	}

	public void moveTo(int x, int z) {
		box = new BoundingBox(x - 4, z - 4, x + 4, z + 4);
	}
}
