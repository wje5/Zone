package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;

import net.minecraft.item.Item;

public class ZoneItem extends Item {
	public ZoneItem(String name) {
		super();
		setRegistryName("zone:" + name);
		setUnlocalizedName(name);
		setCreativeTab(TabZone.tab);
	}
}
