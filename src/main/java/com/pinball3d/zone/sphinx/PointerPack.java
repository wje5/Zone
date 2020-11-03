package com.pinball3d.zone.sphinx;

public class PointerPack extends Pointer {
	public LogisticPack pack;

	public PointerPack(LogisticPack pack) {
		super(new BoundingBox((int) pack.x - 1, (int) pack.z - 1, (int) pack.x + 2, (int) pack.z + 2));
		this.pack = pack;
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, box.x - offsetX, box.y - offsetZ, 107, 32, 5, 5, 0.5F);
	}
}
