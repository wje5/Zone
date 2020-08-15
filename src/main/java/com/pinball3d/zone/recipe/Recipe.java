package com.pinball3d.zone.recipe;

import java.util.List;

import com.pinball3d.zone.recipe.RecipeHandler.Type;
import com.pinball3d.zone.sphinx.Util;

import net.minecraft.item.ItemStack;

public abstract class Recipe {
	protected int time;

	public abstract Type getType();

	public abstract ItemStack getInput(int index);

	public abstract List<ItemStack> getInputs();

	public abstract ItemStack getOutput(int index);

	public int getTime() {
		return time;
	}

	public boolean apply(int index, ItemStack stack2) {
		ItemStack stack = getInput(index);
		if (Util.isItemStackEqualEgnoreCount(stack, stack2)) {
			if (stack2.getCount() >= stack.getCount()) {
				return true;
			}
		}
		return false;
	}
}
