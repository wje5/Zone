package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;

public class LogNeedNetworkDestroyed extends Log {
	private SerialNumber serial;
	private WorldPos pos;

	public LogNeedNetworkDestroyed(int id, SerialNumber serial, WorldPos pos) {
		super(Level.IMPORTANT, Type.NEEDNETWORKDESTROYED, id);
		this.serial = serial;
		this.pos = pos;
	}

	public LogNeedNetworkDestroyed(NBTTagCompound tag) {
		super(tag);
	}

	public SerialNumber getSerial() {
		return serial;
	}

	public WorldPos getPos() {
		return pos;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		serial.check(te);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.need_network_destroyed",
				new LogComponentNeedNetwork(serial), new LogComponentPos(pos));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		serial = new SerialNumber(tag.getCompoundTag("serial"));
		pos = new WorldPos(tag.getCompoundTag("pos"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("serial", serial.writeToNBT(new NBTTagCompound()));
		tag.setTag("pos", pos.writeToNBT(new NBTTagCompound()));
		return tag;
	}
}
