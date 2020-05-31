package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.sphinx.WorldPos;

public interface INeedNetwork {
	public void connect(WorldPos pos);

	public WorldPos getNetwork();
}
