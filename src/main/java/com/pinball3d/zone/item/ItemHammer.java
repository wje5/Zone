package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;

import net.minecraft.item.Item;

public class ItemHammer extends Item {
	public ItemHammer() {
		setRegistryName("hammer");
		setUnlocalizedName("hammer");
		setCreativeTab(TabZone.tab);
	}
}
