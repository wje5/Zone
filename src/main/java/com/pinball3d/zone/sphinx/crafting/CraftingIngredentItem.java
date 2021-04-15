package com.pinball3d.zone.sphinx.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CraftingIngredentItem extends CraftingIngredent {
	private Item item;
	private int count, damage;
	private NBTTagCompound tag;

	public CraftingIngredentItem(NBTTagCompound tag) {
		super(tag);
	}

	public CraftingIngredentItem(Item item, int count) {
		this(item, count, 0);
	}

	public CraftingIngredentItem(Item item, int count, int damage) {
		this(item, count, damage, null);
	}

	public CraftingIngredentItem(Item item, int count, int damage, NBTTagCompound tag) {
		super(Type.ITEM);
		this.item = item;
		this.count = count;
		this.damage = damage;
		if (tag == null || tag.hasNoTags()) {
			this.tag = null;
		} else {
			this.tag = tag;
		}
	}

	public CraftingIngredentItem(ItemStack stack) {
		this(stack.getItem(), stack.getCount(), stack.getItemDamage(), stack.getTagCompound());
	}

	public Item getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

	public boolean isEmpty() {
		return item.equals(Items.AIR);
	}

	public NBTTagCompound getTag() {
		return tag;
	}

	public ItemStack createStack() {
		return new ItemStack(item, 1, damage, tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		item = Item.getByNameOrId(tag.getString("id"));
		if (tag.hasKey("count")) {
			count = tag.getInteger("count");
		} else {
			count = 1;
		}
		if (tag.hasKey("damage")) {
			damage = tag.getInteger("damage");
		} else {
			damage = 0;
		}
		if (tag.hasKey("tag")) {
			this.tag = (NBTTagCompound) tag.getTag("tag");
			if (this.tag.hasNoTags()) {
				this.tag = null;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("id", Item.REGISTRY.getNameForObject(item).toString());
		if (count != 1) {
			tag.setInteger("count", count);
		}
		if (damage != 0) {
			tag.setInteger("damage", damage);
		}
		if (this.tag != null && !tag.hasNoTags()) {
			tag.setTag("tag", this.tag);
		}
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof CraftingIngredentItem
				&& ((CraftingIngredentItem) obj).item.equals(item) && ((CraftingIngredentItem) obj).damage == damage
				&& ((((CraftingIngredentItem) obj).tag == null && tag == null)
						|| (((CraftingIngredentItem) obj).tag != null
								&& ((CraftingIngredentItem) obj).tag.equals(tag)));
	}

	@Override
	public int hashCode() {
		return (((super.hashCode() * 31 + item.hashCode()) * 31 + count) * 31 + damage) * 31
				+ (tag == null || tag.hasNoTags() ? 0 : tag.hashCode());
	}
}
