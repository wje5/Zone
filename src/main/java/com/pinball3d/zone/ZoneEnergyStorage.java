package com.pinball3d.zone;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class ZoneEnergyStorage extends EnergyStorage {
	public ZoneEnergyStorage(int capacity) {
		super(capacity);
	}

	public void readFromNBT(NBTTagCompound tag) {
		energy = tag.getInteger("energy");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("energy", energy);
		return tag;
	}
}
