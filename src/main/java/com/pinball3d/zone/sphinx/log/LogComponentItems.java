package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.subscreen.SubscreenViewItems;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.resources.I18n;

public class LogComponentItems extends LogComponent {
	private StorageWrapper items;

	public LogComponentItems(StorageWrapper items) {
		super(Type.ITEMS);
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
		return I18n.format("log.items", items.getCount());
	}
}
