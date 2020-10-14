package com.pinball3d.zone.recipe;

import java.util.Arrays;
import java.util.List;

import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;

public class RecipeFormingPress extends Recipe {
	private ItemStack[] inputs;
	private ItemStack output;

	public RecipeFormingPress(ItemStack input1, ItemStack input2, ItemStack output, int time) {
		inputs = new ItemStack[] { input1, input2 };
		this.output = output;
		this.time = time;
	}

	@Override
	public Type getType() {
		return Type.FORMING_PRESS;
	}

	@Override
	public ItemStack getInput(int index) {
		return (index > 1 ? ItemStack.EMPTY : inputs[index]).copy();
	}

	@Override
	public ItemStack getOutput(int index) {
		return (index == 0 ? output : ItemStack.EMPTY).copy();
	}

	@Override
	public List<ItemStack> getInputs() {
		return Arrays.asList(inputs);
	}
}
