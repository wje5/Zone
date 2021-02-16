package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.util.StorageWrapper;

public class LogComponentPack extends LogComponent {
	private int id;
	private StorageWrapper items;

	public LogComponentPack(int id, StorageWrapper items) {
		super(Type.PACK);
		this.id = id;
		this.items = items;
	}

	@Override
	public String toString() {
		return "P" + id;
	}
}
