package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ChunkHandler.IChunkLoader;
import com.pinball3d.zone.block.BlockBeaconCore;
import com.pinball3d.zone.sphinx.INode;
import com.pinball3d.zone.util.Util;
import com.pinball3d.zone.util.WorldPos;

public class TEBeaconCore extends TENeedNetwork implements IChunkLoader, INode {
	private boolean full;

	public TEBeaconCore() {

	}

	@Override
	public void update() {
		if (!world.isRemote) {
			boolean old = full;
			full = BlockBeaconCore.isFullStructure(world, pos);
			if (old != full) {
				WorldPos pos = Util.getPosFromUUID(network);
				if (!pos.isOrigin()) {
					((TEProcessingCenter) pos.getTileEntity()).markMapDirty();
				}

			}
		}
		super.update();
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
		state = state == WorkingState.WORKING && !BlockBeaconCore.isFullStructure(world, pos) ? WorkingState.BREAK
				: state;
		return state;
	}
}
