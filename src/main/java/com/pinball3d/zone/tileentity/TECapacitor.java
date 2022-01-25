package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ZoneEnergyStorage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TECapacitor extends TileEntity {
	protected ZoneEnergyStorage energy = new ZoneEnergyStorage(10000);

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			return (T) energy;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound.getCompoundTag("energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}
}
