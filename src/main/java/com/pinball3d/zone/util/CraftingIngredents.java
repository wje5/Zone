package com.pinball3d.zone.util;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public class CraftingIngredents {
	private Item item;
	private int damage;
	private NBTTagCompound tag;

	public CraftingIngredents(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public CraftingIngredents(Item item) {
		this(item, 0);
	}

	public CraftingIngredents(Item item, int damage) {
		this(item, damage, null);
	}

	public CraftingIngredents(Item item, int damage, NBTTagCompound tag) {
		this.item = item;
		this.damage = damage;
		this.tag = tag;
	}

	public Item getItem() {
		return item;
	}

	public int getDamage() {
		return damage;
	}

	public NBTTagCompound getTag() {
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag) {
		item = Item.getByNameOrId(tag.getString("id"));
		damage = tag.getInteger("damage");
		if (tag.hasKey("tag")) {
			this.tag = (NBTTagCompound) tag.getTag("tag");
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("id", Item.REGISTRY.getNameForObject(item).toString());
		tag.setInteger("damage", damage);
		if (this.tag != null) {
			tag.setTag("tag", this.tag);
		}
	}
}
