package com.pinball3d.zone.jei;

import java.util.Arrays;

import com.pinball3d.zone.recipe.RecipeCentrifuge;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeWrapperCentrifuge implements IRecipeWrapper {
	protected final RecipeCentrifuge recipe;

	public RecipeWrapperCentrifuge(RecipeCentrifuge recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, recipe.getInput(0));
		ingredients.setOutputs(VanillaTypes.ITEM,
				Arrays.asList(recipe.getOutput(0), recipe.getOutput(1), recipe.getOutput(2)));
	}
}
