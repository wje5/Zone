package com.pinball3d.zone.sphinx.log.component;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageOreDictionaryPriority;
import com.pinball3d.zone.util.Util;

public class LogComponentOreDictionary extends LogComponent {
	private int id;
	private String name;
	private boolean dead;

	public LogComponentOreDictionary(int id, String name, boolean dead) {
		super(Type.PLAYER);
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
			root.putScreen(new SubscreenManageOreDictionaryPriority(root, null, id));
		}
	}

	@Override
	public int getColor() {
		return dead ? 0xFFFC3D3D : 0xFF3AFAFD;
	}

	@Override
	public String toString() {
		String s = "O" + id;
		if (name != null) {
			s = s + " \"" + name + "\"";
		}
		return s;
	}
}
