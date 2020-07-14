package com.pinball3d.zone.sphinx;

import net.minecraft.tileentity.TileEntity;

public class LogisticPack {
	public WorldPos target;
	public StorageWrapper items;

	public LogisticPack(WorldPos target, StorageWrapper wrapper) {
		this.target = target;
		items = wrapper;
	}

	public boolean check() {
		TileEntity te = target.getTileEntity();
		if (!(te instanceof IStorable) && !(te instanceof IDevice)) {
			return false;
		}
		return true;
	}
}
