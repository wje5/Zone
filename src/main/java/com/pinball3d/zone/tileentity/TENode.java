package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.sphinx.INode;

public class TENode extends TENeedNetwork implements IChunkLoader, INode {
	public TENode() {

	}

	@Override
	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (world.provider.getDimension() == dim && Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		return false;
	}
}
