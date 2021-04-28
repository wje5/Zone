package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerAlloySmelter;
import com.pinball3d.zone.inventory.GuiContainerCentrifuge;
import com.pinball3d.zone.inventory.GuiContainerCrystallizer;
import com.pinball3d.zone.inventory.GuiContainerFormingPress;
import com.pinball3d.zone.inventory.GuiContainerGrinder;
import com.pinball3d.zone.inventory.GuiContainerLathe;
import com.pinball3d.zone.recipe.RecipeAlloySmelter;
import com.pinball3d.zone.recipe.RecipeCentrifuge;
import com.pinball3d.zone.recipe.RecipeCrystallizer;
import com.pinball3d.zone.recipe.RecipeFormingPress;
import com.pinball3d.zone.recipe.RecipeGrinder;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;
import com.pinball3d.zone.recipe.RecipeLathe;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.gui.Focus;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIHandler implements IModPlugin {
	private static IJeiRuntime runtime;

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RecipeCategoryGrinder(helper));
		registry.addRecipeCategories(new RecipeCategoryAlloySmelter(helper));
		registry.addRecipeCategories(new RecipeCategoryCentrifuge(helper));
		registry.addRecipeCategories(new RecipeCategoryCrystallizer(helper));
		registry.addRecipeCategories(new RecipeCategoryLathe(helper));
		registry.addRecipeCategories(new RecipeCategoryFormingPress(helper));
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipes(RecipeHandler.getRecipes(Type.GRINDER), "zone:grinder");
		registry.handleRecipes(RecipeGrinder.class, recipe -> new RecipeWrapperGrinder(recipe), "zone:grinder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.grinder), "zone:grinder");
		registry.addRecipeClickArea(GuiContainerGrinder.class, 80, 35, 22, 15, "zone:grinder");
		registry.addRecipes(RecipeHandler.getRecipes(Type.ALLOY_SMELTER), "zone:alloy_smelter");
		registry.handleRecipes(RecipeAlloySmelter.class, recipe -> new RecipeWrapperAlloySmelter(recipe),
				"zone:alloy_smelter");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.alloy_smelter), "zone:alloy_smelter");
		registry.addRecipeClickArea(GuiContainerAlloySmelter.class, 80, 35, 22, 15, "zone:alloy_smelter");
		registry.addRecipes(RecipeHandler.getRecipes(Type.CENTRIFUGE), "zone:centrifuge");
		registry.handleRecipes(RecipeCentrifuge.class, recipe -> new RecipeWrapperCentrifuge(recipe),
				"zone:centrifuge");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.centrifuge), "zone:centrifuge");
		registry.addRecipeClickArea(GuiContainerCentrifuge.class, 63, 35, 22, 15, "zone:centrifuge");
		registry.addRecipes(RecipeHandler.getRecipes(Type.CRYSTALLIZER), "zone:crystallizer");
		registry.handleRecipes(RecipeCrystallizer.class, recipe -> new RecipeWrapperCrystallizer(recipe),
				"zone:crystallizer");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.crystallizer), "zone:crystallizer");
		registry.addRecipeClickArea(GuiContainerCrystallizer.class, 80, 35, 22, 15, "zone:crystallizer");
		registry.addRecipes(RecipeHandler.getRecipes(Type.LATHE), "zone:lathe");
		registry.handleRecipes(RecipeLathe.class, recipe -> new RecipeWrapperLathe(recipe), "zone:lathe");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.lathe), "zone:lathe");
		registry.addRecipeClickArea(GuiContainerLathe.class, 80, 35, 22, 15, "zone:lathe");
		registry.addRecipes(RecipeHandler.getRecipes(Type.FORMING_PRESS), "zone:forming_press");
		registry.handleRecipes(RecipeFormingPress.class, recipe -> new RecipeWrapperFormingPress(recipe),
				"zone:forming_press");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.forming_press), "zone:forming_press");
		registry.addRecipeClickArea(GuiContainerFormingPress.class, 80, 35, 22, 15, "zone:forming_press");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}

	public static void showJEI(ItemStack stack, boolean isInput) {
		runtime.getRecipesGui().show(new Focus<ItemStack>(isInput ? Mode.INPUT : Mode.OUTPUT, stack));
	}
}
