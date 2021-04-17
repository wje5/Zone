package com.pinball3d.zone.sphinx.log;

import java.util.ArrayList;
import java.util.List;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.log.component.LogComponentItems;
import com.pinball3d.zone.sphinx.log.component.LogComponentPack;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogRecvPackFull extends Log {
	private StorageWrapper items, lostItems;
	private int packId, packTime;
	private List<SerialNumber> path;
	private SerialNumber start, end;

	public LogRecvPackFull(int id, int packId, StorageWrapper items, SerialNumber start, SerialNumber end,
			List<SerialNumber> path, int packTime, StorageWrapper lostItems) {
		super(Level.IMPORTANT, Type.RECVPACKFULL, id);
		this.packId = packId;
		this.items = items.copy();
		this.start = start;
		this.end = end;
		this.path = path;
		this.packTime = packTime;
		this.lostItems = lostItems;
	}

	public LogRecvPackFull(NBTTagCompound tag) {
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

	public SerialNumber getStart() {
		return start;
	}

	public SerialNumber getEnd() {
		return end;
	}

	public int getPackTime() {
		return packTime;
	}

	public StorageWrapper getLostItems() {
		return lostItems;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		start.check(te);
		end.check(te);
		path.forEach(e -> e.check(te));
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.recv_pack_full", new LogComponentPack(packId, getItems()),
				start, path, Util.transferTickToString(packTime), end, new LogComponentItems(lostItems));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		packId = tag.getInteger("packId");
		items = new StorageWrapper(tag.getCompoundTag("items"));
		start = new SerialNumber(tag.getCompoundTag("start"));
		end = new SerialNumber(tag.getCompoundTag("end"));
		path = new ArrayList<SerialNumber>();
		tag.getTagList("path", 10).forEach(e -> path.add(new SerialNumber((NBTTagCompound) e)));
		packTime = tag.getInteger("packTime");
		lostItems = new StorageWrapper(tag.getCompoundTag("lostItems"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("packId", packId);
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		tag.setTag("start", start.writeToNBT(new NBTTagCompound()));
		tag.setTag("end", end.writeToNBT(new NBTTagCompound()));
		NBTTagList list = new NBTTagList();
		path.forEach(e -> {
			list.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("path", list);
		tag.setInteger("packTime", packTime);
		tag.setTag("lostItems", lostItems.writeToNBT(new NBTTagCompound()));
		return tag;
	}
}
