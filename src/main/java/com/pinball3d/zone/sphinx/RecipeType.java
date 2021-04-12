package com.pinball3d.zone.sphinx;

import net.minecraft.nbt.NBTTagCompound;

public class RecipeType {
	private int id, deviceCount;
	private int[] slotCount;
	private String name;
	private boolean isI18N;

	public RecipeType(int id, String name, boolean isI18N, int deviceCount, int[] slotCount) {
		this.id = id;
		this.name = name;
		this.isI18N = isI18N;
		this.deviceCount = deviceCount;
		this.slotCount = slotCount;
		if (slotCount.length != deviceCount) {
			throw new java.lang.IndexOutOfBoundsException();
		}
	}

	public RecipeType(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isI18N() {
		return isI18N;
	}

	public int getDeviceCount() {
		return deviceCount;
	}

	public void readFromNBT(NBTTagCompound tag) {
		id = tag.getInteger("id");
		name = tag.getString("name");
		isI18N = tag.getBoolean("isI18N");
		deviceCount = tag.getInteger("deviceCount");
		slotCount = tag.getIntArray("slotCount");
		if (slotCount.length != deviceCount) {
			throw new java.lang.IndexOutOfBoundsException();
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("id", id);
		tag.setString("name", name);
		tag.setBoolean("isI18N", isI18N);
		tag.setInteger("deviceCount", deviceCount);
		tag.setIntArray("slotCount", slotCount);
		return tag;
	}

	public static enum BasicType {
		MINECRAFT_WORKBENCH, MINECRAFT_FURNACE;
	}
}
