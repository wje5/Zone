package com.pinball3d.zone.sphinx.log.component;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.map.MapHandler;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

public class LogComponentPos extends LogComponent {
	private WorldPos pos;

	public LogComponentPos(WorldPos pos) {
		super(Type.POS);
		this.pos = pos;
	}

	public WorldPos getPos() {
		return pos;
	}

	@Override
	public void onClick() {
		super.onClick();
		IHasSubscreen root = Util.getRoot();
		while (!root.getSubscreens().empty()) {
			root.removeScreen(root.getSubscreens().peek());
		}
		MapHandler.focus(pos.getPos().getX(), pos.getPos().getZ());
	}

	@Override
	public int getColor() {
		return 0xFF3AFAFD;
	}

	@Override
	public String toString() {
		return "[" + pos.toString() + "]";
	}
}
