package com.pinball3d.zone.sphinx.log;

import java.util.ArrayList;
import java.util.List;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogSendPack extends Log {
	private StorageWrapper items;
	private int packId, time;
	private List<SerialNumber> path;

	public LogSendPack(int id, int packId, StorageWrapper items, List<SerialNumber> path, int time) {
		super(Level.DEBUG, Type.SENDPACK, id);
		this.packId = packId;
		this.items = items;
		this.path = path;
		this.time = time;
	}

	public LogSendPack(NBTTagCompound tag) {
		super(tag);
	}

	public int getPackId() {
		return packId;
	}

	public StorageWrapper getItems() {
		return items;
	}

	public List<SerialNumber> getPath() {
		return path;
	}

	public int getTime() {
		return time;
	}

	@Override
	public String toString() {
		String s = "";
		String start = path.isEmpty() ? Util.DATA_CORRUPTION : path.get(0).toString();
		String end = path.isEmpty() ? Util.DATA_CORRUPTION : path.get(path.size() - 1).toString();
		for (int i = 1; i < path.size() - 1; i++) {
			if (!s.isEmpty()) {
				s += ",";
			}
			s += path.get(i);
		}
		return super.toString() + " " + I18n.format("log.send_pack", "P" + packId, start, s, time, end);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		packId = tag.getInteger("packId");
		items = new StorageWrapper(tag.getCompoundTag("items"));
		path = new ArrayList<SerialNumber>();
		tag.getTagList("path", 10).forEach(e -> path.add(new SerialNumber((NBTTagCompound) e)));
		time = tag.getInteger("time");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("packId", packId);
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		NBTTagList list = new NBTTagList();
		path.forEach(e -> {
			list.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setInteger("time", time);
		return tag;
	}
}
