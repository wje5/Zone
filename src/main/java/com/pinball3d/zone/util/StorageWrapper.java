package com.pinball3d.zone.util;

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
				int hash1 = o1.stack.hasTagCompound() ? o1.stack.getTagCompound().hashCode() : 0;
				int hash2 = o2.stack.hasTagCompound() ? o2.stack.getTagCompound().hashCode() : 0;
				return o1.stack.hasTagCompound() && o2.stack.hasTagCompound()
						? hash1 > hash2 ? 1
								: hash2 > hash1 ? -1
										: o1.hashCode() > o2.hashCode() ? 1 : o2.hashCode() > o1.hashCode() ? -1 : 0
						: o1.stack.hasTagCompound() ? 1
								: o2.stack.hasTagCompound() ? -1
										: o1.hashCode() > o2.hashCode() ? 1 : o2.hashCode() > o1.hashCode() ? -1 : 0;
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
				return o1.hasTagCompound() && o2.hasTagCompound()
						? o1.getTagCompound().hashCode() > o2.getTagCompound().hashCode() ? 1
								: o2.getTagCompound().hashCode() > o1.getTagCompound().hashCode() ? -1
										: o1.hashCode() > o2.hashCode() ? 1 : o2.hashCode() > o1.hashCode() ? -1 : 0
						: o1.hasTagCompound() ? 1
								: o2.hasTagCompound() ? -1
										: o1.hashCode() > o2.hashCode() ? 1 : o2.hashCode() > o1.hashCode() ? -1 : 0;
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

	public boolean isEmpty() {
		return storges.isEmpty() && other.isEmpty();
	}

	public void merge(IItemHandler input, boolean isSimulate) {
		int max = input.getSlots();
		for (int i = 0; i < max; i++) {
			ItemStack stack = input.getStackInSlot(i);
			merge(input.extractItem(i, stack.getCount(), isSimulate));
		}
	}

	/** NEVER MODIFY THE INPUT STACK */
	public void merge(ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		if (stack.getMaxStackSize() <= 1) {
			other.add(stack.copy());
		} else {
			Iterator<HugeItemStack> it = storges.iterator();
			while (it.hasNext()) {
				HugeItemStack i = it.next();
				if (i.merge(stack)) {
					return;
				}
			}
			storges.add(new HugeItemStack(stack.copy()));
		}
	}

	/** NEVER MODIFY THE INPUT STACK */
	public void merge(HugeItemStack hugestack) {
		Iterator<HugeItemStack> it = storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			if (i.merge(hugestack)) {
				return;
			}
		}
		storges.add(hugestack.copy());
	}

	/** NEVER MODIFY THE INPUT STACK */
	public void merge(StorageWrapper wrapper) {
		Iterator<HugeItemStack> it = wrapper.storges.iterator();
		while (it.hasNext()) {
			HugeItemStack i = it.next();
			merge(i);
		}
		wrapper.other.forEach(e -> other.add(e.copy()));
		other.addAll(wrapper.other);
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
			if (i.isEmpty()) {
				it.remove();
			}
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

	public int getSize() {
		return storges.size() + other.size();
	}

	public int getCount() {
		int count = 0;
		Iterator<HugeItemStack> it = storges.iterator();
		while (it.hasNext()) {
			count += it.next().count;
		}
		count += other.size();
		return count;
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

	public void search(ItemSample sample) {
		Iterator<HugeItemStack> it = storges.iterator();
		while (it.hasNext()) {
			if (!sample.contains(new ItemType(it.next().stack))) {
				it.remove();
			}
		}
		Iterator<ItemStack> it2 = other.iterator();
		while (it2.hasNext()) {
			if (!sample.contains(new ItemType(it2.next()))) {
				it2.remove();
			}
		}
	}

	public StorageWrapper search(String search) {
		Iterator<HugeItemStack> it = this.storges.iterator();
		while (it.hasNext()) {
			if (!Util.search(search, it.next().stack)) {
				it.remove();
			}
		}
		Iterator<ItemStack> it2 = other.iterator();
		while (it2.hasNext()) {
			if (!Util.search(search, it2.next())) {
				it2.remove();
			}
		}
		return this;
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

	public boolean isEquals(StorageWrapper wrapper) {
		if (storges.size() == wrapper.storges.size() && other.size() == wrapper.other.size()) {
			Iterator<HugeItemStack> i = storges.iterator();
			Iterator<HugeItemStack> i2 = wrapper.storges.iterator();
			while (i.hasNext() && i2.hasNext()) {
				if (!i.next().isEquals(i2.next())) {
					return false;
				}
			}
			Iterator<ItemStack> j = other.iterator();
			Iterator<ItemStack> j2 = wrapper.other.iterator();
			while (j.hasNext() && j2.hasNext()) {
				if (!Util.isItemStackEqualEgnoreCount(j.next(), j2.next())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
