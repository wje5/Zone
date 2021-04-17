package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.ClassifyGroup;
import com.pinball3d.zone.sphinx.log.component.LogComponentClassify;
import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogDeleteClassify extends Log {
	private UUID uuid;
	private String name, classifyName;
	private boolean playerDead, classifyDead;
	private int classifyId;

	public LogDeleteClassify(int id, EntityPlayer player, int classifyId, String classifyName) {
		super(Level.INFO, Type.DELETECLASSIFY, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.classifyId = classifyId;
		this.classifyName = classifyName;
	}

	public LogDeleteClassify(NBTTagCompound tag) {
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

	public int getClassifyId() {
		return classifyId;
	}

	public String getClassifyName() {
		return classifyName;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		playerDead = !te.hasUser(uuid);
		ClassifyGroup g = te.getClassifyGroups().get(classifyId);
		classifyDead = g == null;
		if (g != null) {
			classifyName = g.getName();
		}
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.delete_classify",
				new LogComponentClassify(classifyId, classifyName, classifyDead),
				new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		classifyId = tag.getInteger("classifyId");
		classifyName = tag.getString("classifyName");
		classifyDead = tag.getBoolean("classifyDead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setInteger("classifyId", classifyId);
		tag.setString("classifyName", classifyName);
		tag.setBoolean("classifyDead", classifyDead);
		return tag;
	}
}
