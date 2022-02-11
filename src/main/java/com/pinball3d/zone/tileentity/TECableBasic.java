package com.pinball3d.zone.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TECableBasic extends TileEntity implements ITickable {
	public TECableBasic() {

	}

	@Override
	public void update() {
		if (!world.isRemote) {

		}
	}

	public boolean isConnect(EnumFacing facing) {
		TileEntity te = world.getTileEntity(getPos().offset(facing));
		if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing)) {
			IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			if (s.canExtract() || s.canReceive()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
//		if (CapabilityEnergy.ENERGY.equals(capability)) {
//			return true;
//		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
//		if (CapabilityEnergy.ENERGY.equals(capability)) {
//			return (T) new EnergyIOWrapper(energy, canExtractEnergy(facing), canReceiveEnergy(facing));
//		}
		return super.getCapability(capability, facing);
	}

	public boolean canExtractEnergy(EnumFacing facing) {
		return true;
	}

	public boolean canReceiveEnergy(EnumFacing facing) {
		return true;
	}

	public boolean activeOutput(EnumFacing facing) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		return compound;
	}
}
