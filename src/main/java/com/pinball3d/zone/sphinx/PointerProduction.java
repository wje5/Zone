package com.pinball3d.zone.sphinx;

public class PointerProduction extends PointerNeedNetwork {
	private boolean valid;

	public PointerProduction(WorldPos pos, boolean valid) {
		super(pos);
		this.valid = valid;
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
}
