package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogConnectToNetwork extends Log {
	private UUID uuid;
	private String name;
	private boolean playerDead;
	private SerialNumber serial;
	private WorldPos pos;

	public LogConnectToNetwork(int id, EntityPlayer player, SerialNumber serial, WorldPos pos) {
		super(Level.INFO, Type.CONNECTTONETWORK, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.serial = serial;
		this.pos = pos;
	}

	public LogConnectToNetwork(NBTTagCompound tag) {
		super(tag);
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public boolean isPlayerDead() {
		return playerDead;
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
		playerDead = !te.isUser(uuid);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.connect_to_network", new LogComponentNeedNetwork(serial),
				new LogComponentPos(pos), new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		serial = new SerialNumber(tag.getCompoundTag("serial"));
		pos = new WorldPos(tag.getCompoundTag("pos"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setTag("serial", serial.writeToNBT(new NBTTagCompound()));
		tag.setTag("pos", pos.writeToNBT(new NBTTagCompound()));
		return tag;
	}
}
