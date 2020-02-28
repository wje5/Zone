package com.pinball3d.zone.recipe;

import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;

public class RecipeCentrifuge extends Recipe {
	private ItemStack input;
	private ItemStack output[];

	public RecipeCentrifuge(ItemStack input, ItemStack output1, ItemStack output2, ItemStack output3, int time) {
		this.input = input;
		this.output = new ItemStack[] { output1, output2, output3 };
		this.time = time;
	}

	@Override
	public Type getType() {
		return Type.CENTRIFUGE;
	}

	@Override
	public ItemStack getInput(int index) {
		return (index == 0 ? input : ItemStack.EMPTY).copy();
	}

	@Override
	public ItemStack getOutput(int index) {
		return (index < 3 ? output[index] : ItemStack.EMPTY).copy();
	}
}
