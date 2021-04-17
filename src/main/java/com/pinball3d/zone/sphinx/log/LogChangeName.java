package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.sphinx.log.component.LogComponentString;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogChangeName extends Log {
	private UUID uuid;
	private String sphinxName, name;
	private boolean playerDead;

	public LogChangeName(int id, String sphinxName, EntityPlayer player) {
		super(Level.INFO, Type.CHANGENAME, id);
		uuid = player.getUniqueID();
		this.sphinxName = sphinxName;
		name = player.getName();
	}

	public LogChangeName(NBTTagCompound tag) {
		super(tag);
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getSphinxName() {
		return sphinxName;
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
		return new FormattedLog(getTime(), getLevel(), "log.change_name", new LogComponentString(sphinxName),
				new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		sphinxName = tag.getString("sphinxName");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("sphinxName", sphinxName);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		return tag;
	}
}
