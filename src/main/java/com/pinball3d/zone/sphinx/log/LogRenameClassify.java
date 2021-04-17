package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.log.component.LogComponentClassify;
import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogRenameClassify extends Log {
	private UUID uuid;
	private String name, classifyOldName, classifyNewName;
	private boolean playerDead, classifyDead;
	private int classifyId;

	public LogRenameClassify(int id, EntityPlayer player, int classifyId, String classifyOldName,
			String classifyNewName) {
		super(Level.INFO, Type.RENAMECLASSIFY, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.classifyId = classifyId;
		this.classifyOldName = classifyOldName;
		this.classifyNewName = classifyNewName;
	}

	public LogRenameClassify(NBTTagCompound tag) {
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

	public String getClassifyOldName() {
		return classifyOldName;
	}

	public String getClassifyNewName() {
		return classifyNewName;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		playerDead = !te.hasUser(uuid);
		classifyDead = te.getClassifyGroups().get(classifyId) == null;
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.rename_classify",
				new LogComponentClassify(classifyId, classifyOldName, classifyDead),
				new LogComponentClassify(classifyId, classifyNewName, classifyDead),
				new LogComponentPlayer(uuid, name, playerDead));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		classifyId = tag.getInteger("classifyId");
		classifyOldName = tag.getString("classifyOldName");
		classifyNewName = tag.getString("classifyNewName");
		classifyDead = tag.getBoolean("classifyDead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setInteger("classifyId", classifyId);
		tag.setString("classifyOldName", classifyOldName);
		tag.setString("classifyNewName", classifyNewName);
		tag.setBoolean("classifyDead", classifyDead);
		return tag;
	}
}
