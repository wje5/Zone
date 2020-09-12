package com.pinball3d.zone.sphinx;

public class PointerLiving extends Pointer {
	public boolean isMob;

	public PointerLiving(int x, int z, boolean isMob) {
		super(x, z);
		this.isMob = isMob;
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, x - offsetX - 4, z - offsetZ - 4, isMob ? 0 : 16, 0, 16, 16, 0.5F);
	}
}
