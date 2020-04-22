package com.pinball3d.zone.sphinx;

import net.minecraft.nbt.NBTTagCompound;

public class SphinxDataHandler {
	private WorldPos pos;

	public SphinxDataHandler(NBTTagCompound tag) {
		load(tag);
	}

	public SphinxDataHandler(WorldPos pos) {
		this.pos = pos;
	}

	public WorldPos getPos() {
		return pos;
	}

	public void load(NBTTagCompound tag) {
		pos = WorldPos.load(tag);
	}

	public NBTTagCompound save(NBTTagCompound tag) {
		pos.save(tag);
		return tag;
	}
}
