package com.pinball3d.zone.sphinx.log;

import net.minecraft.nbt.NBTTagCompound;

public class LogSphinxOpenFinish extends Log {
	public LogSphinxOpenFinish(int id) {
		super(Level.IMPORTANT, Type.SPHINXOPENFINISH, id);
	}

	public LogSphinxOpenFinish(NBTTagCompound tag) {
		super(tag);
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.sphinx_open_finish", new LogComponentNetwork());
	}
}
