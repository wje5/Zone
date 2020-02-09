package com.pinball3d.zone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TabZone extends CreativeTabs {
	public static TabZone tab;

	public static void init() {
		tab = new TabZone();
	}

	public TabZone() {
		super("zone");
	}

	@Override
	public ItemStack getTabIconItem() {
		return null;
	}
}
