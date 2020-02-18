package com.pinball3d.zone.recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipeHandler {
	private static Map<Type, Set<Recipe>> map = new HashMap<Type, Set<Recipe>>();

	public static void init() {
		for (Type t : Type.values()) {
			map.put(t, new HashSet<Recipe>());
		}
		register(
				new RecipeGrinder(new ItemStack(Blocks.IRON_ORE), new ItemStack(ItemLoader.crushed_iron_ore, 32), 200));
		register(
				new RecipeGrinder(new ItemStack(Blocks.GOLD_ORE), new ItemStack(ItemLoader.crushed_gold_ore, 32), 200));
		register(new RecipeGrinder(new ItemStack(Blocks.REDSTONE_ORE), new ItemStack(Items.REDSTONE, 16), 200));
		register(new RecipeAlloySmelter(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT),
				new ItemStack(Items.QUARTZ), new ItemStack(ItemLoader.etherium), 200));
	}

	public static void register(Recipe recipe) {
		map.get(recipe.getType()).add(recipe);
	}

	public static Recipe getRecipe(Type type, List<ItemStack> inputs) {
		Set<Recipe> set = map.get(type);
		Iterator<Recipe> it = set.iterator();
		while (it.hasNext()) {
			Recipe recipe = it.next();
			if (match(recipe, inputs)) {
				return recipe;
			}
		}
		return null;
	}

	public static boolean match(Recipe recipe, List<ItemStack> inputs) {
		for (int i = 0; i < inputs.size(); i++) {
			if (!recipe.apply(i, inputs.get(i))) {
				return false;
			}
		}
		if (recipe.getInput(inputs.size()).isEmpty()) {
			return true;
		}
		return false;
	}

	public static enum Type {
		GRINDER, ALLOY_SMELTER;
	}
}
