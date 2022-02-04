package com.pinball3d.zone.capability;

import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyIOWrapper implements IEnergyStorage {
	private boolean canExtract, canReceive;
	private EnergyStorage energy;

	public EnergyIOWrapper(EnergyStorage energy, boolean canExtract, boolean canReceive) {
		this.energy = energy;
		this.canExtract = canExtract;
		this.canReceive = canReceive;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (canReceive()) {
			return energy.receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (canExtract()) {
			return energy.extractEnergy(maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return canExtract;
	}

	@Override
	public boolean canReceive() {
		return canReceive;
	}

}
