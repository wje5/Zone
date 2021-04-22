package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.sphinx.log.component.LogComponentOreDictionary;
import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogNewOreDictionary extends Log {
	private UUID uuid;
	private String name;
	private boolean playerDead, oreDictionaryDead;
	private int oreDictionaryId;

	public LogNewOreDictionary(int id, EntityPlayer player, int oreDictionaryId) {
		super(Level.INFO, Type.NEWOREDICTIONARY, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.oreDictionaryId = oreDictionaryId;
	}

	public LogNewOreDictionary(NBTTagCompound tag) {
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

	public int getOreDictionaryId() {
		return oreDictionaryId;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		playerDead = !te.hasUser(uuid);
		OreDictionaryData g = te.getOreDictionarys().get(oreDictionaryId);
		oreDictionaryDead = g == null;
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.new_ore_dictionary",
				new LogComponentOreDictionary(oreDictionaryId, null, oreDictionaryDead),
				new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		oreDictionaryId = tag.getInteger("oreDictionaryId");
		oreDictionaryDead = tag.getBoolean("oreDictionaryDead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setInteger("oreDictionaryId", oreDictionaryId);
		tag.setBoolean("oreDictionaryDead", oreDictionaryDead);
		return tag;
	}
}
