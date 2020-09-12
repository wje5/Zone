package com.pinball3d.zone.sphinx;

public class PointerNode extends PointerNeedNetwork {
	private boolean valid;

	public PointerNode(WorldPos pos, boolean valid) {
		super(pos);
		this.valid = valid;
	}

	@Override
	public void doRender(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 3, 136, 0, 13, 13,
				0.5F);
		if (!valid) {
			Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX, pos.getPos().getZ() - offsetZ, 116, 21, 9, 9,
					0.5F);
		}
	}
}
