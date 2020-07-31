package com.pinball3d.zone.sphinx;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IStorable {
	public IItemHandler getStorage();

	public default StorageWrapper getStorages() {
		return new StorageWrapper(getStorage(), true);
	}

	public default StorageWrapper extract(StorageWrapper request) {
		IItemHandler inv = getStorage();
		StorageWrapper extracted = new StorageWrapper();
		request.storges.forEach(e -> {
			int amount = e.count;
			for (int i = inv.getSlots() - 1; i >= 0; i--) {
				if (amount <= 0) {
					break;
				}
				ItemStack stack = inv.getStackInSlot(i);
				if (e.stack.isItemEqual(stack)) {
					stack = inv.extractItem(i, amount, false);
					amount -= stack.getCount();
					extracted.merge(stack);
				}
			}
		});
		request.other.forEach(e -> {
			for (int i = inv.getSlots() - 1; i >= 0; i--) {
				if (ItemStack.areItemStacksEqual(inv.getStackInSlot(i), e)) {
					extracted.merge(inv.getStackInSlot(i).copy());
					inv.extractItem(i, e.getCount(), false);
					break;
				}
			}
		});
		StorageWrapper r = extracted.copy();
		request.shrink(extracted);
		return r;
	}

	public default StorageWrapper insert(StorageWrapper wrapper, boolean simulate) {
		IItemHandler inv = getStorage();
		int max = inv.getSlots();
		int[] history = new int[max];
		StorageWrapper inserted = new StorageWrapper();
		wrapper.storges.forEach(e -> {
			int amount = e.count;
			for (int i = 0; i < max; i++) {
				if (amount <= 0) {
					break;
				}
				ItemStack stack = e.stack.copy();
				stack.setCount(amount >= e.stack.getMaxStackSize() ? e.stack.getMaxStackSize() : amount);
				int count = inv.insertItem(i, stack, false).getCount();
				stack = e.stack.copy();
				stack.setCount(amount >= e.stack.getMaxStackSize() ? e.stack.getMaxStackSize() : amount);
				stack.shrink(count);
				amount -= stack.getCount();
				history[i] += stack.getCount();
				inserted.merge(stack);
			}
		});
		wrapper.other.forEach(e -> {
			for (int i = inv.getSlots() - 1; i >= 0; i--) {
				if (inv.insertItem(i, e.copy(), false).isEmpty()) {
					inserted.merge(e.copy());
					history[i] = 1;
					break;
				}
			}
		});
		if (simulate) {
			for (int i = 0; i < history.length; i++) {
				if (history[i] != 0) {
					inv.extractItem(i, history[i], false);
				}
			}
		}
		StorageWrapper r = inserted.copy();
		wrapper.shrink(inserted);
		return r;
	}
}
