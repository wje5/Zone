package com.pinball3d.zone.sphinx;

public class PointerProcessingCenter extends PointerNeedNetwork {

	public PointerProcessingCenter(WorldPos pos) {
		super(pos, 10, 12);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 5, pos.getPos().getZ() - offsetZ - 5, 116, 0, 20, 22,
				0.5F);
	}

	@Override
	public boolean isClick(int x, int z) {
		return pos.getPos().getX() - 5 <= x && pos.getPos().getX() - 5 + width >= x && pos.getPos().getZ() - 5 <= z
				&& pos.getPos().getZ() - 5 + width >= z;
	}
}
