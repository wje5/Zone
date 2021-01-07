package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.block.BlockBeaconCore;
import com.pinball3d.zone.sphinx.INode;

public class TEBeaconCore extends TENeedNetwork implements IChunkLoader, INode {
	private boolean full;

	public TEBeaconCore() {

	}

	@Override
	public void work() {
		boolean old = full;
		full = ((BlockBeaconCore) blockType).isFullStructure(world, pos);
		if (old != full) {
			((TEProcessingCenter) getNetworkPos().getTileEntity()).markMapDirty();
		}
	}

	@Override
	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (full && world.provider.getDimension() == dim && Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		return false;
	}

	@Override
	public WorkingState getWorkingState() {
		WorkingState state = super.getWorkingState();
		state = state == WorkingState.WORKING && !full ? WorkingState.BREAK : state;
		return state;
	}
}
