package com.pinball3d.zone.sphinx;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TerminalDataHandler {
	public EntityPlayer player;

	public TerminalDataHandler(EntityPlayer player) {
		this.player = player;
	}

	public void loadData(NBTTagCompound tag) {

	}

	public NBTTagCompound saveData(NBTTagCompound tag) {
		return tag;
	}
}
