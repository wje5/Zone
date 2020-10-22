package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.block.BlockBeaconControlMatrix;
import com.pinball3d.zone.sphinx.INode;

public class TEBeaconControlMatrix extends TENeedNetwork implements IChunkLoader, INode {
	private boolean full;

	public TEBeaconControlMatrix() {

	}

	@Override
	public void work() {
		full = ((BlockBeaconControlMatrix) blockType).isFullStructure(world, pos);
		if (full) {

		}
	}

	@Override
	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (world.provider.getDimension() == dim && Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		return false;
	}
}
