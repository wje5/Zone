package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.crafting.OreDictionaryData;
import com.pinball3d.zone.sphinx.log.component.LogComponentOreDictionary;
import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogChangeOreDictionaryPriority extends Log {
	private UUID uuid;
	private String name, oreDictionaryName;
	private boolean playerDead, oreDictionaryDead;
	private int oreDictionaryId;

	public LogChangeOreDictionaryPriority(int id, EntityPlayer player, int oreDictionaryId, String oreDictionaryName) {
		super(Level.INFO, Type.CHANGEOREDICTIONARYPRIORITY, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.oreDictionaryId = oreDictionaryId;
		this.oreDictionaryName = oreDictionaryName;
	}

	public LogChangeOreDictionaryPriority(NBTTagCompound tag) {
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

	public String getOreDictionaryName() {
		return oreDictionaryName;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		playerDead = !te.hasUser(uuid);
		OreDictionaryData g = te.getOreDictionarys().get(oreDictionaryId);
		oreDictionaryDead = g == null;
		if (g != null) {
			oreDictionaryName = g.getName();
		}
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.change_ore_dictionary_priority",
				new LogComponentOreDictionary(oreDictionaryId, oreDictionaryName, oreDictionaryDead),
				new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		oreDictionaryId = tag.getInteger("oreDictionaryId");
		if (tag.hasKey("oreDictionaryName")) {
			oreDictionaryName = tag.getString("oreDictionaryName");
		}
		oreDictionaryDead = tag.getBoolean("oreDictionaryDead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setInteger("oreDictionaryId", oreDictionaryId);
		if (oreDictionaryName != null) {
			tag.setString("oreDictionaryName", oreDictionaryName);
		}
		tag.setBoolean("oreDictionaryDead", oreDictionaryDead);
		return tag;
	}
}
