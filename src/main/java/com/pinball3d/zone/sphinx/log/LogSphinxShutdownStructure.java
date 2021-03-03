package com.pinball3d.zone.sphinx.log;

import net.minecraft.nbt.NBTTagCompound;

public class LogSphinxShutdownStructure extends Log {
	public LogSphinxShutdownStructure(int id) {
		super(Level.IMPORTANT, Type.SPHINXSHUTDOWNSTRUCTURE, id);
	}

	public LogSphinxShutdownStructure(NBTTagCompound tag) {
		super(tag);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.sphinx_shutdown_structure", new LogComponentNetwork());
	}
}
