package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.subscreen.SubscreenManageUser;
import com.pinball3d.zone.util.Util;

public class LogComponentPlayer extends LogComponent {
	private UUID uuid;
	private String name;
	private boolean dead;

	public LogComponentPlayer(UUID uuid, String name, boolean dead) {
		super(Type.PLAYER);
		this.uuid = uuid;
		this.name = name;
		this.dead = dead;
	}

	public UUID getUUID() {
		return uuid;
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
			root.putScreen(new SubscreenManageUser(root).setUser(uuid));
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
