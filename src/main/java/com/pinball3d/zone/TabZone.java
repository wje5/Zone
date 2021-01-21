package com.pinball3d.zone;

import com.pinball3d.zone.block.BlockLoader;

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
		return new ItemStack(BlockLoader.io_panel);
	}
}
