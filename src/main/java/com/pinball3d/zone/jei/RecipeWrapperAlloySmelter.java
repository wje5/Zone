package com.pinball3d.zone.jei;

import java.util.Arrays;

import com.pinball3d.zone.recipe.RecipeAlloySmelter;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeWrapperAlloySmelter implements IRecipeWrapper {
	protected final RecipeAlloySmelter recipe;

	public RecipeWrapperAlloySmelter(RecipeAlloySmelter recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM,
				Arrays.asList(recipe.getInput(0), recipe.getInput(1), recipe.getInput(2)));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput(0));
	}
}
