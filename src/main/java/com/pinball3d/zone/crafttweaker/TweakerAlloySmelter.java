package com.pinball3d.zone.crafttweaker;

import java.util.Arrays;

import com.pinball3d.zone.recipe.Recipe;
import com.pinball3d.zone.recipe.RecipeAlloySmelter;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.zone.alloySmelter")
public class TweakerAlloySmelter {
	@ZenMethod
	public static void addRecipe(IItemStack input, IItemStack input2, IItemStack input3, IItemStack output, int time) {
		ItemStack i = CraftTweakerMC.getItemStack(input);
		ItemStack i2 = CraftTweakerMC.getItemStack(input2);
		ItemStack i3 = CraftTweakerMC.getItemStack(input3);
		ItemStack o = CraftTweakerMC.getItemStack(output);
		if (i.isEmpty()) {
			CraftTweakerAPI.logError("Input 1 was Empty.");
		}
		if (i2.isEmpty()) {
			CraftTweakerAPI.logError("Input 2 was Empty.");
		}
		if (i3.isEmpty()) {
			CraftTweakerAPI.logError("Input 3 was Empty.");
		}
		if (o.isEmpty()) {
			CraftTweakerAPI.logError("Output was Empty.");
		}
		RecipeHandler.register(new RecipeAlloySmelter(i, i2, i3, o, time));
	}

	@ZenMethod
	public static void removeRecipe(IItemStack input, IItemStack input2, IItemStack input3) {
		ItemStack i = CraftTweakerMC.getItemStack(input);
		ItemStack i2 = CraftTweakerMC.getItemStack(input2);
		ItemStack i3 = CraftTweakerMC.getItemStack(input3);
		if (i.isEmpty()) {
			CraftTweakerAPI.logError("Input 1 was Empty.");
		}
		if (i2.isEmpty()) {
			CraftTweakerAPI.logError("Input 2 was Empty.");
		}
		if (i3.isEmpty()) {
			CraftTweakerAPI.logError("Input 3 was Empty.");
		}
		Recipe r = RecipeHandler.getRecipe(Type.ALLOY_SMELTER, Arrays.asList(i, i2, i3));
		if (r != null) {
			RecipeHandler.removeRecipe(r);
		}
	}

	@ZenMethod
	public static void removeAll() {
		RecipeHandler.removeAll(Type.ALLOY_SMELTER);
	}
}
