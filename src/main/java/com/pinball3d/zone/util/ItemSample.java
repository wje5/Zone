package com.pinball3d.zone.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemSample {
	private Set<ItemType> set = new TreeSet<ItemType>(ItemType.comparator);

	public ItemSample() {

	}

	public ItemSample(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public ItemSample(StorageWrapper wrapper) {
		wrapper.storges.forEach(e -> {
			set.add(new ItemType(e.stack.getItem(), e.stack.getItemDamage()));
		});
		wrapper.other.forEach(e -> {
			set.add(new ItemType(e.getItem(), e.getItemDamage()));
		});
	}

	public Set<ItemType> get() {
		return set;
	}

	public void addItem(ItemType item) {
		set.add(item);
	}

	public boolean contains(ItemType item) {
		return set.contains(item);
	}

	public void and(ItemSample sample) {
		Iterator<ItemType> it = set.iterator();
		while (it.hasNext()) {
			if (!sample.set.contains(it.next())) {
				it.remove();
			}
		}
	}

	public void or(ItemSample sample) {
		set.addAll(sample.set);
	}

	public void remove(ItemSample sample) {
		Iterator<ItemType> it = set.iterator();
		while (it.hasNext()) {
			if (sample.set.contains(it.next())) {
				it.remove();
			}
		}
	}

	public void clear() {
		set.clear();
	}

	public void readFromNBT(NBTTagCompound tag) {
		set.clear();
		tag.getTagList("items", 10).forEach(e -> set.add(new ItemType((NBTTagCompound) e)));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		set.forEach(e -> list.appendTag(e.writeToNBT(new NBTTagCompound())));
		tag.setTag("items", list);
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ItemSample && set.equals(((ItemSample) obj).set);
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}
}
