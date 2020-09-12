package com.pinball3d.zone.sphinx;

public class PointerPack extends Pointer {
	public PointerPack(int x, int z) {
		super(x, z);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, x - offsetX - 3, z - offsetZ - 3, 136, 0, 13, 13, 0.5F);
	}
}
