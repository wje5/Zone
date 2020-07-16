package com.pinball3d.zone.sphinx;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

	public HugeItemStack(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public boolean merge(ItemStack stack) {
		if (this.stack.isItemEqual(stack)) {
			count += stack.getCount();
			return true;
		}
		return false;
	}

	public boolean merge(HugeItemStack hugestack) {
		if (stack.isItemEqual(hugestack.stack)) {
			count += stack.getCount();
			return true;
		}
		return false;
	}

	public ItemStack shrink(ItemStack stack) {
		if (this.stack.isItemEqual(stack)) {
			if (count >= stack.getCount()) {
				count -= stack.getCount();
				stack.setCount(0);
			} else {
				stack.shrink(count);
				count = 0;
			}
		}
		return stack;
	}

	public HugeItemStack shrink(HugeItemStack hugestack) {
		if (stack.isItemEqual(hugestack.stack)) {
			if (count >= hugestack.count) {
				count -= hugestack.count;
				hugestack.count = 0;
			} else {
				hugestack.count -= count;
				count = 0;
			}
		}
		return hugestack;
	}

	public boolean isEmpty() {
		return count <= 0 || stack.isEmpty();
	}

	public HugeItemStack copy() {
		return new HugeItemStack(stack.copy(), count);
	}

	@Override
	public String toString() {
		return "H{" + stack + "|" + count + "}";
	}

	public void readFromNBT(NBTTagCompound tag) {
		count = tag.getInteger("count");
		stack = new ItemStack(tag.getCompoundTag("stack"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("count", count);
		NBTTagCompound tag2 = new NBTTagCompound();
		stack.writeToNBT(tag2);
		tag.setTag("stack", tag2);
		return tag;
	}
}
