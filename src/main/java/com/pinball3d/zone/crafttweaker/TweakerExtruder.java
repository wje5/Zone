package com.pinball3d.zone.crafttweaker;

import java.util.Arrays;

import com.pinball3d.zone.recipe.Recipe;
import com.pinball3d.zone.recipe.RecipeExtruder;
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
@ZenClass("mods.zone.extruder")
public class TweakerExtruder {
	@ZenMethod
	public static void addRecipe(IItemStack input, IItemStack output, int time) {
		ItemStack i = CraftTweakerMC.getItemStack(input);
		ItemStack o = CraftTweakerMC.getItemStack(output);
		if (i.isEmpty()) {
			CraftTweakerAPI.logError("Input was Empty.");
		}
		if (o.isEmpty()) {
			CraftTweakerAPI.logError("Output was Empty.");
		}
		RecipeHandler.register(new RecipeExtruder(i, o, time));
	}

	@ZenMethod
	public static void removeRecipe(IItemStack input) {
		ItemStack i = CraftTweakerMC.getItemStack(input);
		if (i.isEmpty()) {
			CraftTweakerAPI.logError("Input was Empty.");
		}
		Recipe r = RecipeHandler.getRecipe(Type.EXTRUDER, Arrays.asList(i));
		if (r != null) {
			RecipeHandler.removeRecipe(r);
		}
	}

	@ZenMethod
	public static void removeAll() {
		RecipeHandler.removeAll(Type.EXTRUDER);
	}
}
