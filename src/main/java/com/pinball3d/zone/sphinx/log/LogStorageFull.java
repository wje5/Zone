package com.pinball3d.zone.sphinx.log;

import net.minecraft.nbt.NBTTagCompound;

public class LogStorageFull extends Log {
	public LogStorageFull(int id) {
		super(Level.IMPORTANT, Type.STORAGEFULL, id);
	}

	public LogStorageFull(NBTTagCompound tag) {
		super(tag);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.storage_full");
	}
}
