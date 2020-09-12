package com.pinball3d.zone.sphinx;

public class PointerProcessingCenter extends PointerNeedNetwork {

	public PointerProcessingCenter(WorldPos pos) {
		super(pos);
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 5, pos.getPos().getZ() - offsetZ - 5, 116, 0, 20, 22,
				0.5F);
	}
}
