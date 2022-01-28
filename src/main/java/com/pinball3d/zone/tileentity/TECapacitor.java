package com.pinball3d.zone.tileentity;

import net.minecraft.util.EnumFacing;

public class TECapacitor extends ZoneTieredMachine {
	public TECapacitor() {
		super();
	}

	public TECapacitor(Tier tier) {
		super(tier, 1600000);
	}

	@Override
	public boolean activeOutput(EnumFacing facing) {
		return facing == EnumFacing.UP ? true : super.activeOutput(facing);
	}
}
