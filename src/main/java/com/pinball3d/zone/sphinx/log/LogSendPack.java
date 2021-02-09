package com.pinball3d.zone.sphinx.log;

import java.util.List;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.util.StorageWrapper;

import net.minecraft.nbt.NBTTagCompound;

public class LogSendPack extends Log {
	private StorageWrapper items;
	private int packId;
	private List<SerialNumber> path;

	public LogSendPack(int id, int packId, StorageWrapper items, List<SerialNumber> path) {
		super(Level.DEBUG, Type.SENDPACK, id);
		this.items = items;
		this.path = path;
	}

	public LogSendPack(NBTTagCompound tag) {
		super(tag);
	}

	public StorageWrapper getItems() {
		return items;
	}

	public int getPackId() {
		return packId;
	}

	public List<SerialNumber> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return super.toString() + " ";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		items = new StorageWrapper(tag.getCompoundTag("items"));
		packId = tag.getInteger("packId");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		tag.setInteger("packId", packId);
		return tag;
	}
}
