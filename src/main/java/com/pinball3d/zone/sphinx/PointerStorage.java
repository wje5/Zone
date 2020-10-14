package com.pinball3d.zone.sphinx;

public class PointerStorage extends PointerNeedNetwork {
	public PointerStorage(WorldPos pos, int id, boolean valid) {
		super(pos, id, valid, 7, 7);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 3, 149, 0, 13, 13,
				0.5F);
		if (!valid) {
			Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX, pos.getPos().getZ() - offsetZ, 116, 21, 9, 9,
					0.5F);
		}
	}

	@Override
	public boolean isClick(int x, int z) {
		return pos.getPos().getX() - 3 <= x && pos.getPos().getX() - 3 + width >= x && pos.getPos().getZ() - 3 <= z
				&& pos.getPos().getZ() - 3 + width >= z;
	}
}
