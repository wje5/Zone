package com.pinball3d.zone.sphinx.crafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class OreDictionaryData {
	private String name;
	private CraftingIngredentItem[] items, disableItems;

	public OreDictionaryData(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public OreDictionaryData(String name, CraftingIngredentItem[] items, CraftingIngredentItem[] disableItems) {
		this.name = name;
		this.items = items;
		this.disableItems = disableItems;
	}

	public String getName() {
		return name;
	}

	public CraftingIngredentItem[] getItems() {
		return items;
	}

	public CraftingIngredentItem[] getDisableItems() {
		return disableItems;
	}

	public boolean isOreDictionary() {
		return name == null;
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("name")) {
			name = tag.getString("name");
		} else {
			name = null;
		}
		NBTTagList list = tag.getTagList("items", 10);
		items = new CraftingIngredentItem[list.tagCount()];
		for (int i = 0; i < items.length; i++) {
			items[i] = new CraftingIngredentItem(list.getCompoundTagAt(i));
		}
		list = tag.getTagList("disableItems", 10);
		disableItems = new CraftingIngredentItem[list.tagCount()];
		for (int i = 0; i < disableItems.length; i++) {
			disableItems[i] = new CraftingIngredentItem(list.getCompoundTagAt(i));
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		if (name != null) {
			tag.setString("name", name);
		}
		NBTTagList list = new NBTTagList();
		for (CraftingIngredentItem i : items) {
			list.appendTag(i.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("items", list);
		NBTTagList list2 = new NBTTagList();
		for (CraftingIngredentItem i : disableItems) {
			list2.appendTag(i.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("disableItems", list2);
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OreDictionaryData) {
			OreDictionaryData o = (OreDictionaryData) obj;
			if (((o.name == null && name == null) || (o.name != null && o.name.equals(name)))
					&& o.items.length == items.length && o.disableItems.length == disableItems.length) {
				for (int i = 0; i < items.length; i++) {
					if (!o.items[i].equals(items[i])) {
						return false;
					}
				}
				for (int i = 0; i < disableItems.length; i++) {
					if (!o.disableItems[i].equals(disableItems[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = name == null ? 0 : name.hashCode();
		for (CraftingIngredent i : items) {
			hash = hash * 31 + (i == null ? 0 : i.hashCode());
		}
		hash = hash * 31;
		for (CraftingIngredent i : disableItems) {
			hash = hash * 31 + (i == null ? 0 : i.hashCode());
		}
		return hash;
	}
}
