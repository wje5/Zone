package com.pinball3d.zone.sphinx.map;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.INeedNetwork.WorkingState;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

public class PointerBeacon extends PointerNode {
	public PointerBeacon(WorldPos pos, SerialNumber serial, int id, WorkingState state) {
		super(pos, serial, id, state);
		box = new BoundingBox(pos.getPos().getX() - 3, pos.getPos().getZ() - 3, pos.getPos().getX() + 6,
				pos.getPos().getZ() + 7);
	}

	@Override
	public void render(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 3, 195, 0, 18, 20,
				0.5F);
	}
}
