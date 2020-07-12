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

	public StorageWrapper(IItemHandler stacks) {
		this();
		merge(stacks);
	}

	public void merge(IItemHandler input) {
		int max = input.getSlots();
		for (int i = 0; i < max; i++) {
			ItemStack stack = input.getStackInSlot(i);
			merge(stack);
		}
	}

	public void merge(ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		if (stack.getMaxStackSize() <= 1) {
			other.add(stack);
		} else {
			Iterator<HugeItemStack> it = storges.iterator();
			while (it.hasNext()) {
				HugeItemStack i = it.next();
				if (i.merge(stack)) {
					return;
				}
			}
			storges.add(new HugeItemStack(stack));
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
		storges.add(hugestack);
	}

	public void merge(StorageWrapper wrapper) {
		Iterator<HugeItemStack> it = wrapper.storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			merge(i);
		}
		other.addAll(wrapper.other);
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
