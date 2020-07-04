package com.pinball3d.zone.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

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
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == EnumFacing.DOWN) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == EnumFacing.DOWN) {
			return (T) energy;
		}
		return super.getCapability(capability, facing);
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
