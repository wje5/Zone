package com.pinball3d.zone.recipe;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class RecipeDrillInstall extends ShapelessRecipes {
	public RecipeDrillInstall() {
		super("", new ItemStack(ItemLoader.drill), NonNullList.from(null, Ingredient.fromItem(ItemLoader.drill_head),
				Ingredient.fromItem(ItemLoader.drill_empty)));
		setRegistryName("zone", "drill_install");
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack r = super.getCraftingResult(inv);
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ItemLoader.drill_head) {
				r.setItemDamage(stack.getItemDamage());
			} else if (stack.getItem() == ItemLoader.drill_empty) {
				NBTTagCompound tag = r.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					r.setTagCompound(tag);
				}
				tag.setTag("ench", stack.getEnchantmentTagList());
			}
		}
		return r;
	}
}
