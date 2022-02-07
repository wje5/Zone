package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerAlloySmelter;
import com.pinball3d.zone.inventory.GuiContainerCentrifuge;
import com.pinball3d.zone.inventory.GuiContainerCharger;
import com.pinball3d.zone.inventory.GuiContainerCrystallizer;
import com.pinball3d.zone.inventory.GuiContainerElecFurnace;
import com.pinball3d.zone.inventory.GuiContainerExtruder;
import com.pinball3d.zone.inventory.GuiContainerFormingPress;
import com.pinball3d.zone.inventory.GuiContainerGrinder;
import com.pinball3d.zone.recipe.RecipeAlloySmelter;
import com.pinball3d.zone.recipe.RecipeCentrifuge;
import com.pinball3d.zone.recipe.RecipeCharger;
import com.pinball3d.zone.recipe.RecipeCrystallizer;
import com.pinball3d.zone.recipe.RecipeExtruder;
import com.pinball3d.zone.recipe.RecipeFormingPress;
import com.pinball3d.zone.recipe.RecipeGrinder;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.gui.Focus;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIHandler implements IModPlugin {
	private static IJeiRuntime runtime;

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RecipeCategoryAlloySmelter(helper));
		registry.addRecipeCategories(new RecipeCategoryCentrifuge(helper));
		registry.addRecipeCategories(new RecipeCategoryCharger(helper));
		registry.addRecipeCategories(new RecipeCategoryCrystallizer(helper));
		registry.addRecipeCategories(new RecipeCategoryExtruder(helper));
		registry.addRecipeCategories(new RecipeCategoryFormingPress(helper));
		registry.addRecipeCategories(new RecipeCategoryGrinder(helper));
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipes(RecipeHandler.getRecipes(Type.ALLOY_SMELTER), "zone:alloy_smelter");
		registry.handleRecipes(RecipeAlloySmelter.class, recipe -> new RecipeWrapperAlloySmelter(recipe),
				"zone:alloy_smelter");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.alloy_smelter_1), "zone:alloy_smelter");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.alloy_smelter_2), "zone:alloy_smelter");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.alloy_smelter_3), "zone:alloy_smelter");
		registry.addRecipeClickArea(GuiContainerAlloySmelter.class, 80, 35, 22, 15, "zone:alloy_smelter");

		registry.addRecipes(RecipeHandler.getRecipes(Type.CENTRIFUGE), "zone:centrifuge");
		registry.handleRecipes(RecipeCentrifuge.class, recipe -> new RecipeWrapperCentrifuge(recipe),
				"zone:centrifuge");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.centrifuge_1), "zone:centrifuge");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.centrifuge_2), "zone:centrifuge");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.centrifuge_3), "zone:centrifuge");
		registry.addRecipeClickArea(GuiContainerCentrifuge.class, 63, 35, 22, 15, "zone:centrifuge");

		registry.addRecipes(RecipeHandler.getRecipes(Type.CHARGER), "zone:charger");
		registry.handleRecipes(RecipeCharger.class, recipe -> new RecipeWrapperCharger(recipe), "zone:charger");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.charger_1), "zone:charger");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.charger_2), "zone:charger");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.charger_3), "zone:charger");
		registry.addRecipeClickArea(GuiContainerCharger.class, 80, 35, 22, 15, "zone:charger");

		registry.addRecipes(RecipeHandler.getRecipes(Type.CRYSTALLIZER), "zone:crystallizer");
		registry.handleRecipes(RecipeCrystallizer.class, recipe -> new RecipeWrapperCrystallizer(recipe),
				"zone:crystallizer");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.crystallizer_1), "zone:crystallizer");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.crystallizer_2), "zone:crystallizer");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.crystallizer_3), "zone:crystallizer");
		registry.addRecipeClickArea(GuiContainerCrystallizer.class, 80, 35, 22, 15, "zone:crystallizer");

		registry.addRecipeCatalyst(new ItemStack(BlockLoader.elec_furnace_1), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.elec_furnace_2), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.elec_furnace_3), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiContainerElecFurnace.class, 80, 35, 22, 15, VanillaRecipeCategoryUid.SMELTING);

		registry.addRecipes(RecipeHandler.getRecipes(Type.EXTRUDER), "zone:extruder");
		registry.handleRecipes(RecipeExtruder.class, recipe -> new RecipeWrapperExtruder(recipe), "zone:extruder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.extruder_1), "zone:extruder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.extruder_2), "zone:extruder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.extruder_3), "zone:extruder");
		registry.addRecipeClickArea(GuiContainerExtruder.class, 80, 35, 22, 15, "zone:extruder");

		registry.addRecipes(RecipeHandler.getRecipes(Type.FORMING_PRESS), "zone:forming_press");
		registry.handleRecipes(RecipeFormingPress.class, recipe -> new RecipeWrapperFormingPress(recipe),
				"zone:forming_press");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.forming_press_1), "zone:forming_press");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.forming_press_2), "zone:forming_press");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.forming_press_3), "zone:forming_press");
		registry.addRecipeClickArea(GuiContainerFormingPress.class, 80, 35, 22, 15, "zone:forming_press");

		registry.addRecipes(RecipeHandler.getRecipes(Type.GRINDER), "zone:grinder");
		registry.handleRecipes(RecipeGrinder.class, recipe -> new RecipeWrapperGrinder(recipe), "zone:grinder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.grinder_1), "zone:grinder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.grinder_2), "zone:grinder");
		registry.addRecipeCatalyst(new ItemStack(BlockLoader.grinder_3), "zone:grinder");
		registry.addRecipeClickArea(GuiContainerGrinder.class, 80, 35, 22, 15, "zone:grinder");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}

	public static void showJEI(ItemStack stack, boolean isInput) {
		runtime.getRecipesGui().show(new Focus<ItemStack>(isInput ? Mode.INPUT : Mode.OUTPUT, stack));
	}
}
