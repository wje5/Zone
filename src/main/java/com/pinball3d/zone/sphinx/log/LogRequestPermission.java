package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogRequestPermission extends Log {
	private UUID uuid;
	private String name;
	private boolean playerDead;

	public LogRequestPermission(int id, EntityPlayer player) {
		super(Level.IMPORTANT, Type.REQUESTPERMISSION, id);
		uuid = player.getUniqueID();
		name = player.getName();
	}

	public LogRequestPermission(NBTTagCompound tag) {
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

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		playerDead = !te.hasUser(uuid);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.request_permission",
				new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		return tag;
	}
}
