package com.pinball3d.zone.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TEDrainer extends ZoneMachine {
	protected int tick;

	public TEDrainer() {
		super(1);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		tick++;
		if (tick >= 1200) {
			tick -= 1200;
			addEnergy(1);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tick = compound.getInteger("tick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("tick", tick);
		return compound;
	}
}
