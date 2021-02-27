package com.pinball3d.zone.crafttweaker;

import java.util.Arrays;

import com.pinball3d.zone.recipe.Recipe;
import com.pinball3d.zone.recipe.RecipeCentrifuge;
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
@ZenClass("mods.zone.centrifuge")
public class TweakerCentrifuge {
	@ZenMethod
	public static void addRecipe(IItemStack input, IItemStack output, IItemStack output2, IItemStack output3,
			int time) {
		ItemStack i = CraftTweakerMC.getItemStack(input);
		ItemStack o = CraftTweakerMC.getItemStack(output);
		ItemStack o2 = CraftTweakerMC.getItemStack(output2);
		ItemStack o3 = CraftTweakerMC.getItemStack(output3);
		if (i.isEmpty()) {
			CraftTweakerAPI.logError("Input was Empty.");
		}
		if (o.isEmpty()) {
			CraftTweakerAPI.logError("Output 1 was Empty.");
		}
		if (o2.isEmpty()) {
			CraftTweakerAPI.logError("Output 2 was Empty.");
		}
		if (o3.isEmpty()) {
			CraftTweakerAPI.logError("Output 3 was Empty.");
		}
		RecipeHandler.register(new RecipeCentrifuge(i, o, o2, o3, time));
	}

	@ZenMethod
	public static void removeRecipe(IItemStack input) {
		ItemStack i = CraftTweakerMC.getItemStack(input);
		if (i.isEmpty()) {
			CraftTweakerAPI.logError("Input was Empty.");
		}
		Recipe r = RecipeHandler.getRecipe(Type.CENTRIFUGE, Arrays.asList(i));
		if (r != null) {
			RecipeHandler.removeRecipe(r);
		}
	}

	@ZenMethod
	public static void removeAll() {
		RecipeHandler.removeAll(Type.CENTRIFUGE);
	}
}
