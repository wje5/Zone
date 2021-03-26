package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewItems;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

public class LogComponentPack extends LogComponent {
	private int id;
	private StorageWrapper items;

	public LogComponentPack(int id, StorageWrapper items) {
		super(Type.PACK);
		this.id = id;
		this.items = items;
	}

	@Override
	public void onClick() {
		super.onClick();
		IHasSubscreen root = Util.getRoot();
		root.putScreen(new SubscreenViewItems(root, items));
	}

	@Override
	public String toString() {
		return "P" + id;
	}
}
