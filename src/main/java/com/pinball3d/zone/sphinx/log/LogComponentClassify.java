package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageClassify;
import com.pinball3d.zone.util.Util;

public class LogComponentClassify extends LogComponent {
	private int id;
	private String name;
	private boolean dead;

	public LogComponentClassify(int id, String name, boolean dead) {
		super(Type.CLASSIFY);
		this.id = id;
		this.name = name;
		this.dead = dead;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isDead() {
		return dead;
	}

	@Override
	public void onClick() {
		super.onClick();
		if (!dead) {
			IHasSubscreen root = Util.getRoot();
			root.putScreen(new SubscreenManageClassify(root).setId(id));
		}
	}

	@Override
	public int getColor() {
		return dead ? 0xFFFC3D3D : 0xFF3AFAFD;
	}

	@Override
	public String toString() {
		return name;
	}
}
