package com.pinball3d.zone.jei;

import com.pinball3d.zone.recipe.RecipeGrinder;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeWrapperGrinder implements IRecipeWrapper {
	protected final RecipeGrinder recipe;

	public RecipeWrapperGrinder(RecipeGrinder recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, recipe.getInput(0));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput(0));
	}
}
