package com.pinball3d.zone.sphinx.log;

import net.minecraft.nbt.NBTTagCompound;

public class LogSphinxShutdownEnergy extends Log {
	public LogSphinxShutdownEnergy(int id) {
		super(Level.IMPORTANT, Type.SPHINXSHUTDOWNENERGY, id);
	}

	public LogSphinxShutdownEnergy(NBTTagCompound tag) {
		super(tag);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.sphinx_shutdown_energy", new LogComponentNetwork());
	}
}
