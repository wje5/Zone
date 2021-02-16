package com.pinball3d.zone.sphinx.map;

import java.util.List;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.component.ButtonUnitDelete;
import com.pinball3d.zone.tileentity.INeedNetwork.WorkingState;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

public class PointerDevice extends PointerNeedNetwork {
	public PointerDevice(WorldPos pos, SerialNumber serial, int id, WorkingState state) {
		super(pos, serial, id, state, new BoundingBox(pos.getPos().getX() - 3, pos.getPos().getZ() - 3,
				pos.getPos().getX() + 4, pos.getPos().getZ() + 4));
	}

	@Override
	public List<Component> getUnitButtons(IHasComponents parent) {
		List<Component> l = super.getUnitButtons(parent);
		l.add(new ButtonUnitDelete(parent, this, 0, 0));
		return l;
	}

	@Override
	public void render(int offsetX, int offsetZ) {
		Util.drawTexture(TEXTURE, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 3, 169, 0, 13, 13,
				0.5F);
	}
}
