package com.pinball3d.zone.tileentity;

public class TENode extends TENeedNetwork {
	public TENode() {

	}

	public boolean isPointInRange(int dim, double x, double y, double z) {
		if (world.provider.getDimension() == dim && Math.sqrt(pos.distanceSq(x, y, z)) < 25) {
			return true;
		}
		return false;
	}
}
