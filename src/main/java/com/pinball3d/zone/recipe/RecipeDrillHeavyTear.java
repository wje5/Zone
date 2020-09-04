package com.pinball3d.zone.recipe;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class RecipeDrillHeavyTear extends ShapelessRecipes {
	public RecipeDrillHeavyTear() {
		super("", new ItemStack(ItemLoader.drill_heavy_head),
				NonNullList.from(null, Ingredient.fromItem(ItemLoader.drill_heavy)));
		setRegistryName("zone", "drill_heavy_tear");
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack r = super.getCraftingResult(inv);
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				r.setItemDamage(stack.getItemDamage());
				return r;
			}
		}
		return r;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList list = super.getRemainingItems(inv);
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				ItemStack r = new ItemStack(ItemLoader.drill_empty);
				NBTTagCompound tag = r.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					r.setTagCompound(tag);
				}
				tag.setTag("ench", stack.getEnchantmentTagList());
				list.set(i, r);
				return list;
			}
		}
		return list;
	}
}
