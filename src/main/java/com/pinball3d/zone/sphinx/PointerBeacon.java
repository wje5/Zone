package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.tileentity.INeedNetwork.WorkingState;

public class PointerBeacon extends PointerNode {
	public PointerBeacon(WorldPos pos, int id, WorkingState state) {
		super(pos, id, state);
		box = new BoundingBox(pos.getPos().getX() - 3, pos.getPos().getZ() - 3, pos.getPos().getX() + 6,
				pos.getPos().getZ() + 7);
	}

	@Override
	public void render(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 3, 195, 0, 18, 20,
				0.5F);
	}
}
