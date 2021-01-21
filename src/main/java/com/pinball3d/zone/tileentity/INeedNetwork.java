package com.pinball3d.zone.tileentity;

import java.util.UUID;

import net.minecraft.client.resources.I18n;

public interface INeedNetwork {
	public void connect(UUID uuid);

	public UUID getNetwork();

	public boolean isConnected();

	public void setConnected(boolean connected);

	public void deleteNetwork();

	public WorkingState getWorkingState();

	public String getName();

	public static enum WorkingState {
		WORKING("sphinx.working"), DISCONNECTED("sphinx.disconnected"), BREAK("sphinx.break");

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
