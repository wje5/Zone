package com.pinball3d.zone.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class ZoneEnergyStorage extends EnergyStorage {
	public ZoneEnergyStorage(int capacity) {
		super(capacity);
	}

	public void readFromNBT(NBTTagCompound tag) {
		energy = tag.getInteger("energy");
		capacity = tag.getInteger("capacity");
		maxReceive = capacity;
		maxExtract = capacity;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("energy", energy);
		tag.setInteger("capacity", capacity);
		return tag;
	}
}
