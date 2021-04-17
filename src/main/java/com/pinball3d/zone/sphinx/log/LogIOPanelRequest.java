package com.pinball3d.zone.sphinx.log;

import java.util.UUID;

import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.log.component.LogComponentItems;
import com.pinball3d.zone.sphinx.log.component.LogComponentNeedNetwork;
import com.pinball3d.zone.sphinx.log.component.LogComponentPlayer;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class LogIOPanelRequest extends Log {
	private UUID uuid;
	private String name;
	private boolean playerDead;
	private SerialNumber ioPanel;
	private StorageWrapper items;
	private int totalTime;

	public LogIOPanelRequest(int id, EntityPlayer player, SerialNumber ioPanel, StorageWrapper items, int totalTime) {
		super(Level.INFO, Type.IOPANELREQUEST, id);
		uuid = player.getUniqueID();
		name = player.getName();
		this.ioPanel = ioPanel;
		this.items = items.copy();
		this.totalTime = totalTime;
	}

	public LogIOPanelRequest(NBTTagCompound tag) {
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

	public SerialNumber getIoPanel() {
		return ioPanel;
	}

	public StorageWrapper getItems() {
		return items;
	}

	public int getPackTime() {
		return totalTime;
	}

	@Override
	public void check(TEProcessingCenter te) {
		super.check(te);
		ioPanel.check(te);
		playerDead = !te.hasUser(uuid);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.io_panel_request",
				new LogComponentPlayer(uuid, name, playerDead), new LogComponentNeedNetwork(ioPanel),
				new LogComponentItems(getItems()), Util.transferTickToString(totalTime));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		uuid = tag.getUniqueId("uuid");
		name = tag.getString("name");
		playerDead = tag.getBoolean("dead");
		ioPanel = new SerialNumber(tag.getCompoundTag("ioPanel"));
		items = new StorageWrapper(tag.getCompoundTag("items"));
		totalTime = tag.getInteger("totalTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setUniqueId("uuid", uuid);
		tag.setString("name", name);
		tag.setBoolean("dead", playerDead);
		tag.setTag("ioPanel", ioPanel.writeToNBT(new NBTTagCompound()));
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		tag.setInteger("totalTime", totalTime);
		return tag;
	}
}
