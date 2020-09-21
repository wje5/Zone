package com.pinball3d.zone.item;

import net.minecraft.item.ItemStack;

public class ItemDrillEmpty extends ZoneItem {
	public ItemDrillEmpty() {
		super("drill_empty");
		setUnlocalizedName("drill");
		setMaxStackSize(1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemEnchantability() {
		return 32;
	}
}
