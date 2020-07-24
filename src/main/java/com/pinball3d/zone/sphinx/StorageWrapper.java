package com.pinball3d.zone.sphinx;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;

public class StorageWrapper {
	public Set<HugeItemStack> storges;
	public Set<ItemStack> other;
	public static Comparator<HugeItemStack> hugeStackComparator = new Comparator<HugeItemStack>() {
		@Override
		public int compare(HugeItemStack o1, HugeItemStack o2) {
			int a = Item.getIdFromItem(o1.stack.getItem());
			int b = Item.getIdFromItem(o2.stack.getItem());
			if (a > b) {
				return 1;
			} else if (b > a) {
				return -1;
			} else {
				int c = o1.stack.getItemDamage();
				int d = o2.stack.getItemDamage();
				if (c > d) {
					return 1;
				} else if (d > c) {
					return -1;
				}
				return o1.stack.hashCode() > o2.stack.hashCode() ? 1
						: o1.stack.hashCode() < o2.stack.hashCode() ? -1 : 0;
			}
		};
	};
	public static Comparator<ItemStack> stackComparator = new Comparator<ItemStack>() {
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			int a = Item.getIdFromItem(o1.getItem());
			int b = Item.getIdFromItem(o2.getItem());
			if (a > b) {
				return 1;
			} else if (b > a) {
				return -1;
			} else {
				int c = o1.getItemDamage();
				int d = o2.getItemDamage();
				if (c > d) {
					return 1;
				} else if (d > c) {
					return -1;
				}
				return o1.hashCode() > o2.hashCode() ? 1 : o1.hashCode() < o2.hashCode() ? -1 : 0;
			}
		}
	};

	public StorageWrapper() {
		storges = new TreeSet<HugeItemStack>(hugeStackComparator);
		other = new TreeSet<ItemStack>(stackComparator);
	}

	public StorageWrapper(ItemStack stack) {
		this();
		merge(stack);
	}

	public StorageWrapper(HugeItemStack stack) {
		this();
		merge(stack);
	}

	public StorageWrapper(IItemHandler stacks, boolean isSimulate) {
		this();
		merge(stacks, isSimulate);
	}

	public StorageWrapper(NBTTagCompound tag) {
		this();
		readFromNBT(tag);
	}

	public void merge(IItemHandler input, boolean isSimulate) {
		int max = input.getSlots();
		for (int i = 0; i < max; i++) {
			ItemStack stack = input.getStackInSlot(i);
			merge(stack.copy());
			if (!isSimulate) {
				input.extractItem(i, Integer.MAX_VALUE, false);
			}
		}
	}

	public void merge(ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		if (stack.getMaxStackSize() <= 1) {
			other.add(stack.copy());
			stack.setCount(0);
		} else {
			Iterator<HugeItemStack> it = storges.iterator();
			while (it.hasNext()) {
				HugeItemStack i = it.next();
				if (i.merge(stack)) {
					return;
				}
			}
			storges.add(new HugeItemStack(stack.copy()));
			stack.setCount(0);
		}
	}

	public void merge(HugeItemStack hugestack) {
		Iterator<HugeItemStack> it = storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			if (i.merge(hugestack)) {
				return;
			}
		}
		storges.add(hugestack.copy());
		hugestack.count = 0;
	}

	public void merge(StorageWrapper wrapper) {
		Iterator<HugeItemStack> it = wrapper.storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			merge(i);
		}
		other.addAll(wrapper.other);
		wrapper.clear();
	}

	public void shrink(StorageWrapper wrapper) {
		Iterator<HugeItemStack> it = wrapper.storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			shrink(i);
			if (i.isEmpty()) {
				it.remove();
			}
		}
		Iterator<ItemStack> it2 = wrapper.other.iterator();
		while (it2.hasNext()) {
			ItemStack i = it2.next();
			shrink(i);
			if (i.isEmpty()) {
				it2.remove();
			}
		}
	}

	public HugeItemStack shrink(HugeItemStack hugestack) {
		Iterator<HugeItemStack> it = storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			i.shrink(hugestack);
			if (hugestack.isEmpty()) {
				break;
			}
		}
		return hugestack;
	}

	public ItemStack shrink(ItemStack stack) {
		if (stack.isEmpty()) {
			return stack;
		}
		if (stack.getMaxStackSize() <= 1) {
			Iterator<ItemStack> it = other.iterator();
			while (it.hasNext()) {
				ItemStack i = it.next();
				if (ItemStack.areItemStacksEqual(i, stack)) {
					stack.setCount(0);
					it.remove();
					break;
				}
			}
		} else {
			Iterator<HugeItemStack> it = storges.iterator();
			while (it.hasNext()) {
				HugeItemStack i = it.next();
				i.shrink(stack);
				if (stack.isEmpty()) {
					break;
				}
			}
		}
		return stack;
	}

	public void clear() {
		storges.clear();
		other.clear();
	}

	public StorageWrapper copy() {
		StorageWrapper wrapper = new StorageWrapper();
		storges.forEach(e -> {
			wrapper.storges.add(e.copy());
		});
		other.forEach(e -> {
			wrapper.other.add(e.copy());
		});
		return wrapper;
	}

	@Override
	public String toString() {
		return "SW{storges:" + storges + " other:" + other + "}";
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList storgelist = tag.getTagList("storges", 10);
		storges.clear();
		storgelist.forEach(e -> {
			storges.add(new HugeItemStack((NBTTagCompound) e));
		});
		NBTTagList otherlist = tag.getTagList("other", 10);
		other.clear();
		otherlist.forEach(e -> {
			other.add(new ItemStack((NBTTagCompound) e));
		});
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList storgelist = new NBTTagList();
		storges.forEach(e -> {
			storgelist.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("storges", storgelist);
		NBTTagList otherlist = new NBTTagList();
		other.forEach(e -> {
			otherlist.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("other", otherlist);
		return tag;
	}
}
