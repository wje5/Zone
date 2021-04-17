package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogDeleteUser extends Log {
	private UUID uuid, uuid2;
	private String name, name2;
	private boolean playerDead, playerDead2;

	public LogDeleteUser(int id, EntityPlayer player, UUID uuid2, String name2) {
		super(Level.IMPORTANT, Type.DELETEUSER, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.uuid2 = uuid2;
		this.name2 = name2;
	}

	public LogDeleteUser(NBTTagCompound tag) {
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

	public UUID getUUID2() {
		return uuid2;
	}

	public String getName2() {
		return name2;
	}

	public boolean isPlayerDead2() {
		return playerDead2;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		playerDead = !te.hasUser(uuid);
		playerDead2 = !te.hasUser(uuid2);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.delete_user",
				new LogComponentPlayer(uuid, name, playerDead), new LogComponentPlayer(uuid2, name2, playerDead2));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		uuid2 = tag.getUniqueId("uuid2");
		name2 = tag.getString("name2");
		playerDead2 = tag.getBoolean("dead2");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setUniqueId("uuid2", uuid2);
		tag.setString("name2", name2);
		tag.setBoolean("dead2", playerDead2);
		return tag;
	}
}
