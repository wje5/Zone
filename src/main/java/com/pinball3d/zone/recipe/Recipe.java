package com.pinball3d.zone.recipe;

import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;

public abstract class Recipe {
	protected int time;

	public abstract Type getType();

	public abstract ItemStack getInput(int index);

	public abstract ItemStack getOutput(int index);

	public int getTime() {
		return time;
	}

	public boolean apply(int index, ItemStack stack2) {
		ItemStack stack = getInput(index);
		if (stack.getItem() == stack2.getItem()) {
			int meta = stack.getMetadata();
			if (meta == 32767 || meta == stack2.getMetadata()) {
				return true;
			}
		}
		return false;
	}

	public boolean inputsEqual(Recipe recipe) {
		for (int i = 0;; i++) {
			ItemStack stack = recipe.getInput(i);
			if (getInput(i) == ItemStack.EMPTY || stack == ItemStack.EMPTY) {
				return true;
			}
			if (!apply(i, stack)) {
				return false;
			}
		}
	}
}
