package com.pinball3d.zone.sphinx;

import java.util.List;

import com.pinball3d.zone.tileentity.INeedNetwork.WorkingState;

public class PointerNode extends PointerNeedNetwork {
	public PointerNode(WorldPos pos, int id, WorkingState state) {
		super(pos, id, state, new BoundingBox(pos.getPos().getX() - 3, pos.getPos().getZ() - 3, pos.getPos().getX() + 4,
				pos.getPos().getZ() + 4));
	}

	@Override
	public List<Component> getUnitButtons(IParent parent) {
		List<Component> l = super.getUnitButtons(parent);
		l.add(new ButtonUnitDelete(parent, this, 0, 0));
		return l;
	}

	@Override
	public void render(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 3, 136, 0, 13, 13,
				0.5F);
	}
}
