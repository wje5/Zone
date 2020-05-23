package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerAlloySmelter;
import com.pinball3d.zone.inventory.GuiContainerCentrifuge;
import com.pinball3d.zone.inventory.GuiContainerCrystallizer;
import com.pinball3d.zone.inventory.GuiContainerGrinder;
import com.pinball3d.zone.recipe.RecipeAlloySmelter;
import com.pinball3d.zone.recipe.RecipeCentrifuge;
import com.pinball3d.zone.recipe.RecipeCrystallizer;
import com.pinball3d.zone.recipe.RecipeGrinder;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIHandler implements IModPlugin {
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RecipeCategoryGrinder(helper));
		registry.addRecipeCategories(new RecipeCategoryAlloySmelter(helper));
		registry.addRecipeCategories(new RecipeCategoryCentrifuge(helper));
		registry.addRecipeCategories(new RecipeCategoryCrystallizer(helper));
	}

	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers helpers = registry.getJeiHelpers();
		registry.addRecipes(RecipeHandler.getRecipes(Type.GRINDER), "zone:grinder");
		registry.handleRecipes(RecipeGrinder.class, recipe -> new RecipeWrapperGrinder(helpers, recipe),
				"zone:grinder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.grinder), "zone:grinder");
		registry.addRecipeClickArea(GuiContainerGrinder.class, 80, 35, 22, 15, "zone:grinder");
		registry.addRecipes(RecipeHandler.getRecipes(Type.ALLOY_SMELTER), "zone:alloy_smelter");
		registry.handleRecipes(RecipeAlloySmelter.class, recipe -> new RecipeWrapperAlloySmelter(helpers, recipe),
				"zone:alloy_smelter");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.alloy_smelter), "zone:alloy_smelter");
		registry.addRecipeClickArea(GuiContainerAlloySmelter.class, 80, 35, 22, 15, "zone:alloy_smelter");
		registry.addRecipes(RecipeHandler.getRecipes(Type.CENTRIFUGE), "zone:centrifuge");
		registry.handleRecipes(RecipeCentrifuge.class, recipe -> new RecipeWrapperCentrifuge(helpers, recipe),
				"zone:centrifuge");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.centrifuge), "zone:centrifuge");
		registry.addRecipeClickArea(GuiContainerCentrifuge.class, 63, 35, 22, 15, "zone:centrifuge");
		registry.addRecipes(RecipeHandler.getRecipes(Type.CRYSTALLIZER), "zone:crystallizer");
		registry.handleRecipes(RecipeCrystallizer.class, recipe -> new RecipeWrapperCrystallizer(helpers, recipe),
				"zone:crystallizer");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.crystallizer), "zone:crystallizer");
		registry.addRecipeClickArea(GuiContainerCrystallizer.class, 80, 35, 22, 15, "zone:crystallizer");
	}
}
