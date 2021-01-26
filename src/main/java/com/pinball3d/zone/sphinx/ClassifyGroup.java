package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.util.ItemSample;
import com.pinball3d.zone.util.ItemType;

import net.minecraft.nbt.NBTTagCompound;

public class ClassifyGroup {
	private String name;
	private ItemSample items = new ItemSample();

	public ClassifyGroup(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public ClassifyGroup(String name) {
		this.name = name;
	}

	public ClassifyGroup(String name, ItemSample s) {
		this(name);
		this.addAll(s);
	}

	public void addItem(ItemType item) {
		items.addItem(item);
	}

	public void addAll(ItemSample s) {
		items.or(s);
	}

	public void removeItem(ItemType item) {
		ItemSample s = new ItemSample();
		s.addItem(item);
		items.remove(s);
	}

	public void removeAll(ItemSample s) {
		items.remove(s);
	}

	public void clear() {
		items.clear();
	}

	public String getName() {
		return name;
	}

	public boolean contains(ItemType item) {
		return items.contains(item);
	}

	public ItemSample getItems() {
		return items;
	}

	public void readFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		items.readFromNBT((NBTTagCompound) tag.getTag("items"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		tag.setTag("items", items.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ClassifyGroup && ((ClassifyGroup) obj).name.equals(name)
				&& ((ClassifyGroup) obj).items.equals(items);
	}

	@Override
	public int hashCode() {
		return items.hashCode() * 31 + name.hashCode();
	}
}
