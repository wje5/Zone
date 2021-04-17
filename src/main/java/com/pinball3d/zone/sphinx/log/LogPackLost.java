package com.pinball3d.zone.sphinx.log;

import java.util.ArrayList;
import java.util.List;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.log.component.LogComponentPack;
import com.pinball3d.zone.sphinx.log.component.LogComponentPos;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogPackLost extends Log {
	private StorageWrapper items;
	private int packId;
	private List<SerialNumber> path;
	private SerialNumber start;
	private WorldPos pos;

	public LogPackLost(int id, int packId, StorageWrapper items, SerialNumber start, List<SerialNumber> path,
			WorldPos pos) {
		super(Level.IMPORTANT, Type.PACKLOST, id);
		this.packId = packId;
		this.items = items.copy();
		this.start = start;
		this.path = path;
		this.pos = pos;
	}

	public LogPackLost(NBTTagCompound tag) {
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

	public WorldPos getPos() {
		return pos;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		start.check(te);
		path.forEach(e -> e.check(te));
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.pack_lost", new LogComponentPack(packId, getItems()), start,
				path, new LogComponentPos(pos));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		packId = tag.getInteger("packId");
		items = new StorageWrapper(tag.getCompoundTag("items"));
		start = new SerialNumber(tag.getCompoundTag("start"));
		path = new ArrayList<SerialNumber>();
		tag.getTagList("path", 10).forEach(e -> path.add(new SerialNumber((NBTTagCompound) e)));
		pos = new WorldPos(tag.getCompoundTag("pos"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("packId", packId);
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		tag.setTag("start", start.writeToNBT(new NBTTagCompound()));
		NBTTagList list = new NBTTagList();
		path.forEach(e -> {
			list.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("path", list);
		tag.setTag("pos", pos.writeToNBT(new NBTTagCompound()));
		return tag;
	}
}
