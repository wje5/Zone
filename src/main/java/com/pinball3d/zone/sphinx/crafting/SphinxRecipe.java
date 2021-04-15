package com.pinball3d.zone.sphinx.crafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class SphinxRecipe {
	private int type;
	private CraftingIngredent[] data;

	public SphinxRecipe(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public SphinxRecipe(int type, CraftingIngredent[] data) {
		this.type = type;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public CraftingIngredent[] getData() {
		return data;
	}

	public void readFromNBT(NBTTagCompound tag) {
		type = tag.getInteger("type");
		NBTTagList list = tag.getTagList("data", 9);
		data = new CraftingIngredent[list.tagCount()];
		for (int i = 0; i < data.length; i++) {
			NBTTagCompound t = list.getCompoundTagAt(i);
			if (t.hasNoTags()) {
				data[i] = null;
			} else {
				data[i] = CraftingIngredent.readLogFromNBT(t);
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("type", type);
		NBTTagList list = new NBTTagList();
		for (CraftingIngredent i : data) {
			if (i != null) {
				list.appendTag(i.writeToNBT(new NBTTagCompound()));
			} else {
				list.appendTag(new NBTTagCompound());
			}

		}
		tag.setTag("data", list);
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SphinxRecipe && ((SphinxRecipe) obj).type == type
				&& ((SphinxRecipe) obj).data.length == data.length) {
			CraftingIngredent[] l = ((SphinxRecipe) obj).data;
			for (int i = 0; i < data.length; i++) {
				if (!((l[i] == null && data[i] == null) || (l[i] != null && l[i].equals(data[i])))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = type;
		for (CraftingIngredent i : data) {
			hash = hash * 31 + (i == null ? 0 : i.hashCode());
		}
		return hash;
	}

	@Override
	public String toString() {
		return writeToNBT(new NBTTagCompound()).toString();
	}
}
