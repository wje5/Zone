package com.pinball3d.zone.sphinx.map;

import java.util.List;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Component;
import com.pinball3d.zone.sphinx.INeedNetwork.WorkingState;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.component.ButtonUnitDelete;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

public class PointerProduction extends PointerNeedNetwork {
	public PointerProduction(WorldPos pos, SerialNumber serial, int id, WorkingState state) {
		super(pos, serial, id, state, new BoundingBox(pos.getPos().getX() - 3, pos.getPos().getZ() - 2,
				pos.getPos().getX() + 4, pos.getPos().getZ() + 2));
	}

	@Override
	public List<Component> getUnitButtons(IHasComponents parent) {
		List<Component> l = super.getUnitButtons(parent);
		l.add(new ButtonUnitDelete(parent, this, 0, 0));
		return l;
	}

	@Override
	public void render(int offsetX, int offsetZ) {
		Util.drawTexture(SPHINX_ICONS, pos.getPos().getX() - offsetX - 3, pos.getPos().getZ() - offsetZ - 2, 182, 0, 13, 8,
				0.5F);
	}
}
