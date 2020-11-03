package com.pinball3d.zone.sphinx;

import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IStorable {
	public IItemHandler getStorage();

	public default StorageWrapper getStorages() {
		return new StorageWrapper(getStorage(), true);
	}

	public default StorageWrapper extract(StorageWrapper request, boolean isSimulate) {
		IItemHandler inv = getStorage();
		StorageWrapper extracted = new StorageWrapper();
		for (int i = inv.getSlots() - 1; i >= 0; i--) {
			ItemStack stack = inv.getStackInSlot(i);
			int amount = stack.getCount();
			Iterator<HugeItemStack> it = request.storges.iterator();
			while (it.hasNext()) {
				HugeItemStack e = it.next();
				if (amount > 0 && !e.isEmpty() && Util.isItemStackEqualEgnoreCount(e.stack, stack)) {
					if (isSimulate) {
						if (e.count >= amount) {
							e.count -= amount;
							if (e.isEmpty()) {
								it.remove();
							}
							amount = 0;
							extracted.merge(stack.copy());
						} else {
							amount -= e.count;
							ItemStack copy = stack.copy();
							copy.setCount(e.count);
							it.remove();
							extracted.merge(copy);
						}
					} else {
						ItemStack s = inv.extractItem(i, e.count, false);
						e.count -= s.getCount();
						extracted.merge(s);
					}
				}
			}
			Iterator<ItemStack> j = request.other.iterator();
			while (j.hasNext()) {
				ItemStack e = j.next();
				if (amount > 0 && !e.isEmpty() && Util.isItemStackEqualEgnoreCount(e, stack)) {
					if (isSimulate) {
						if (e.getCount() >= amount) {
							e.shrink(amount);
							if (e.isEmpty()) {
								j.remove();
							}
							amount = 0;
							extracted.merge(stack.copy());
						} else {
							amount -= e.getCount();
							extracted.merge(e);
							j.remove();
						}
					} else {
						stack = inv.extractItem(i, e.getCount(), false);
						e.shrink(stack.getCount());
						extracted.merge(stack);
					}
				}
			}
		}
//		request.storges.forEach(e -> {
//			int amount = e.count;
//			for (int i = inv.getSlots() - 1; i >= 0; i--) {
//				if (amount <= 0) {
//					break;
//				}
//				ItemStack stack = inv.getStackInSlot(i);
//				if (Util.isItemStackEqualEgnoreCount(e.stack, stack)) {
//					stack = inv.extractItem(i, amount, false);
//					amount -= stack.getCount();
//					extracted.merge(stack);
//				}
//			}
//		});
//		request.other.forEach(e -> {
//			for (int i = inv.getSlots() - 1; i >= 0; i--) {
//				if (ItemStack.areItemStacksEqual(inv.getStackInSlot(i), e)) {
//					extracted.merge(inv.getStackInSlot(i).copy());
//					inv.extractItem(i, e.getCount(), false);
//					break;
//				}
//			}
//		});
		return extracted;
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
