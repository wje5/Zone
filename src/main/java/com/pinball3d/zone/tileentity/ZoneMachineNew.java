package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.EnergyIOWrapper;
import com.pinball3d.zone.ZoneEnergyStorage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ZoneMachineNew extends TileEntity implements ITickable {
	protected ZoneEnergyStorage energy;

	public ZoneMachineNew() {
		energy = new ZoneEnergyStorage(0);
	}

	public ZoneMachineNew(int maxEnergy) {
		energy = new ZoneEnergyStorage(maxEnergy);
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			int amount = energy.extractEnergy(energy.getEnergyStored(), true);
			if (amount > 0) {
				for (EnumFacing facing : EnumFacing.VALUES) {
					if (activeOutput(facing)) {
						TileEntity te = world.getTileEntity(getPos().offset(facing));
						if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing)) {
							IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
							System.out.println(amount);
							amount = s.receiveEnergy(amount, false);
							System.out.println(amount + "|");
							energy.extractEnergy(amount, false);
						}
					}
				}
			}
		}
	}

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
			return (T) new EnergyIOWrapper(energy, canExtractEnergy(facing), canReceiveEnergy(facing));
		}
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
		energy.readFromNBT(compound.getCompoundTag("energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("energy", energy.writeToNBT(new NBTTagCompound()));
		return compound;
	}
}
