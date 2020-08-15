package com.pinball3d.zone.recipe;

import java.util.List;

import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

public class RecipeAlloySmelter extends Recipe {
	private ItemStack[] inputs;
	private ItemStack output;

	public RecipeAlloySmelter(ItemStack input1, ItemStack input2, ItemStack input3, ItemStack output, int time) {
		inputs = new ItemStack[] { input1, input2, input3 };
		this.output = output;
		this.time = time;
	}

	@Override
	public Type getType() {
		return Type.ALLOY_SMELTER;
	}

	@Override
	public ItemStack getInput(int index) {
		return (index > 2 ? ItemStack.EMPTY : inputs[index]).copy();
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
