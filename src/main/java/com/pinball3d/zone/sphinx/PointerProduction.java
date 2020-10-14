package com.pinball3d.zone.sphinx;

public class PointerProduction extends PointerNeedNetwork {
	public PointerProduction(WorldPos pos, int id, boolean valid) {
		super(pos, id, valid, 7, 4);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 2, 182, 0, 13, 8,
				0.5F);
		if (!valid) {
			Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX, pos.getPos().getZ() - offsetZ + 1, 116, 21, 9, 9,
					0.5F);
		}
	}

	@Override
	public boolean isClick(int x, int z) {
		return pos.getPos().getX() - 3 <= x && pos.getPos().getX() - 3 + width >= x && pos.getPos().getZ() - 2 <= z
				&& pos.getPos().getZ() - 2 + width >= z;
	}
}
