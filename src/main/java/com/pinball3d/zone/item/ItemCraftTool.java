package com.pinball3d.zone.item;

import net.minecraft.item.ItemStack;

public class ItemCraftTool extends ZoneItem {
	public ItemCraftTool(String name) {
		super(name);
		setMaxStackSize(1);
		setMaxDamage(60);
		setContainerItem(this);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		stack = stack.copy();
		stack.attemptDamageItem(1, itemRand, null);
		return stack;
	}
}
