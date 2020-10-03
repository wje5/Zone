package com.pinball3d.zone.tileentity;

import java.util.UUID;

import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.client.resources.I18n;

public interface INeedNetwork {
	public void connect(UUID uuid, String password);

	public UUID getNetwork();

	public void setWorldPos(WorldPos pos, UUID uuid);

	public WorldPos getNetworkPos();

	public boolean isConnected();

	public void setConnected(boolean connected);

	public void deleteNetwork();

	public String getPassword();

	public void setPassword(String password);

	public WorkingState getWorkingState();

	public String getName();

	public static enum WorkingState {
		WORKING("sphinx.working"), DISCONNECTED("sphinx.disconnected");

		String key;

		private WorkingState(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return I18n.format(key);
		}
	}
}
