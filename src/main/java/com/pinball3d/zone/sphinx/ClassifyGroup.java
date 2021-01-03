package com.pinball3d.zone.sphinx;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.pinball3d.zone.util.ItemType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ClassifyGroup {
	private String name;
	private Set<ItemType> items = new TreeSet<ItemType>(ItemType.comparator);

	public ClassifyGroup(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public ClassifyGroup(String name) {
		this.name = name;
	}

	public ClassifyGroup(String name, Collection<? extends ItemType> c) {
		this(name);
		this.addAll(c);
	}

	public void addItem(ItemType item) {
		items.add(item);
	}

	public void addAll(Collection<? extends ItemType> c) {
		items.addAll(c);
	}

	public String getName() {
		return name;
	}

	public boolean contains(ItemType item) {
		return items.contains(item);
	}

	public Set<ItemType> getItems() {
		return items;
	}

	public void readFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		items.clear();
		NBTTagList list = tag.getTagList("items", 10);
		list.forEach(e -> {
			items.add(new ItemType((NBTTagCompound) e));
		});
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		NBTTagList list = new NBTTagList();
		items.forEach(e -> {
			list.appendTag(e.writeToNBT(new NBTTagCompound()));
		});
		tag.setTag("items", list);
		return tag;
	}
}
