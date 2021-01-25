package com.pinball3d.zone.util;

import java.util.Comparator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemType {
	public final Item item;
	public final int meta;

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
			return o1.item.getHasSubtypes() && o2.item.getHasSubtypes() ? o1.meta - o2.meta : 0;
		}
	};

	public ItemType(NBTTagCompound tag) {
		this(tag.getInteger("item"), tag.getInteger("meta"));
	}

	public ItemType(int item, int meta) {
		this(Item.getItemById(item), meta);
	}

	public ItemType(Item item, int meta) {
		this.item = item;
		this.meta = meta;
	}

	public ItemStack createStack() {
		return new ItemStack(item, 1, meta);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("item", Item.getIdFromItem(item));
		tag.setInteger("meta", meta);
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ItemType && item.equals(((ItemType) obj).item) && meta == ((ItemType) obj).meta;
	}
}
