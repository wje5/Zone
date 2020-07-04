package com.pinball3d.zone.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TETransmissionModule extends ZoneMachine {
	public TETransmissionModule() {
		super(9);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			switch (facing) {
			case UP:
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				return true;
			}
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			switch (facing) {
			case UP:
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				return (T) energy;
			}
		}
		return super.getCapability(capability, facing);
	}
}
