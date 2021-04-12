package com.pinball3d.zone.util;

import java.util.Comparator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemType {
	public final Item item;
	public final int damage;

	public static Comparator<ItemType> comparator = new Comparator<ItemType>() {
		@Override
		public int compare(ItemType o1, ItemType o2) {
			int i = Item.getIdFromItem(o1.item);
			int i2 = Item.getIdFromItem(o2.item);
			if (i < i2) {
				return -1;
			}
			if (i > i2) {
				return 1;
			}
			return o1.item.getHasSubtypes() && o2.item.getHasSubtypes() ? o1.damage - o2.damage : 0;
		}
	};

	public ItemType(NBTTagCompound tag) {
		this(Item.getByNameOrId(tag.getString("id")), tag.getInteger("damage"));
	}

	public ItemType(Item item, int damage) {
		this.item = item;
		this.damage = damage;
	}

	public ItemType(Item item) {
		this(item, 0);
	}

	public ItemType(Block block, int damage) {
		this(Item.getItemFromBlock(block), damage);
	}

	public ItemType(Block block) {
		this(block, 0);
	}

	public ItemType(ItemStack stack) {
		this(stack.getItem(), stack.getItemDamage());
	}

	public ItemStack createStack() {
		return new ItemStack(item, 1, damage);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("item", Item.getIdFromItem(item));
		tag.setInteger("damage", damage);
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ItemType && item.equals(((ItemType) obj).item) && damage == ((ItemType) obj).damage;
	}

	@Override
	public int hashCode() {
		return item.hashCode() * 31 + damage;
	}
}
