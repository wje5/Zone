package com.pinball3d.zone.sphinx;

import net.minecraft.item.ItemStack;

public class HugeItemStack {
	public int count;
	public ItemStack stack;

	public HugeItemStack(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public HugeItemStack(ItemStack stack, int count) {
		this.count = count;
		this.stack = stack;
	}

	public boolean merge(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() == this.stack.getItem()
				&& stack.getItemDamage() == this.stack.getItemDamage()) {
			count += stack.getCount();
			return true;
		}
		return false;
	}

	public boolean merge(HugeItemStack hugestack) {
		if (hugestack.stack.getItem() == this.stack.getItem()
				&& hugestack.stack.getItemDamage() == this.stack.getItemDamage()) {
			count += stack.getCount();
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "H{" + stack + "|" + count + "}";
	}
}
