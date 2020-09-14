package com.pinball3d.zone.sphinx;

public class PointerPack extends Pointer {
	public PointerPack(int x, int z) {
		super(x, z);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, x - offsetX - 2, z - offsetZ - 2, 107, 32, 5, 5, 0.5F);
	}
}
