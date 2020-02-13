package com.pinball3d.zone.recipe;

import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;

public class RecipeGrinder extends Recipe {
	private ItemStack input, output;

	public RecipeGrinder(ItemStack input, ItemStack output, int time) {
		this.input = input;
		this.output = output;
		this.time = time;
	}

	@Override
	public Type getType() {
		return Type.GRINDER;
	}

	@Override
	public ItemStack getInput(int index) {
		return index == 0 ? input : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getOutput(int index) {
		return index == 0 ? output : ItemStack.EMPTY;
	}
}