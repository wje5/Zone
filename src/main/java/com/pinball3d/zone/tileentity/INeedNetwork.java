package com.pinball3d.zone.tileentity;

import java.util.UUID;

import com.pinball3d.zone.sphinx.WorldPos;

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
}
