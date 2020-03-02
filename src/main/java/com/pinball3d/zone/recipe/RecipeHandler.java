package com.pinball3d.zone.recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinball3d.zone.block.BlockLoader;
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
		register(new RecipeGrinder(new ItemStack(Blocks.DIAMOND_ORE), new ItemStack(ItemLoader.crushed_diamond_ore, 32),
				200));
		register(new RecipeGrinder(new ItemStack(Blocks.EMERALD_ORE), new ItemStack(ItemLoader.crushed_emerald_ore, 32),
				200));
		register(new RecipeGrinder(new ItemStack(Blocks.REDSTONE_ORE),
				new ItemStack(ItemLoader.crushed_redstone_ore, 32), 200));
		register(
				new RecipeGrinder(new ItemStack(Blocks.COAL_ORE), new ItemStack(ItemLoader.crushed_coal_ore, 32), 200));
		register(new RecipeGrinder(new ItemStack(Blocks.LAPIS_ORE), new ItemStack(ItemLoader.crushed_lapis_ore, 32),
				200));
		register(new RecipeGrinder(new ItemStack(Blocks.QUARTZ_ORE), new ItemStack(ItemLoader.crushed_quartz_ore, 32),
				200));
		register(new RecipeGrinder(new ItemStack(Items.IRON_INGOT), new ItemStack(ItemLoader.tiny_pile_iron_dust, 8),
				200));
		register(new RecipeGrinder(new ItemStack(Items.GOLD_INGOT), new ItemStack(ItemLoader.tiny_pile_gold_dust, 8),
				200));
		register(new RecipeGrinder(new ItemStack(Items.DIAMOND), new ItemStack(ItemLoader.tiny_pile_diamond_dust, 8),
				200));
		register(new RecipeGrinder(new ItemStack(Items.COAL), new ItemStack(ItemLoader.tiny_pile_coal_dust, 8), 200));
		register(new RecipeGrinder(new ItemStack(Items.DYE, 1, 4), new ItemStack(ItemLoader.tiny_pile_lapis_dust, 8),
				200));
		register(new RecipeGrinder(new ItemStack(Items.QUARTZ), new ItemStack(ItemLoader.tiny_pile_quartz_dust, 8),
				200));
		register(new RecipeGrinder(new ItemStack(Items.IRON_NUGGET), new ItemStack(ItemLoader.tiny_pile_iron_dust, 1),
				200));
		register(new RecipeGrinder(new ItemStack(Items.GOLD_NUGGET), new ItemStack(ItemLoader.tiny_pile_gold_dust, 1),
				200));
		register(new RecipeGrinder(new ItemStack(ItemLoader.etherium),
				new ItemStack(ItemLoader.tiny_pile_etherium_dust, 8), 200));
		register(new RecipeGrinder(new ItemStack(BlockLoader.clarity_glass),
				new ItemStack(ItemLoader.tiny_pile_clarity_glass_dust, 8), 200));
		register(new RecipeAlloySmelter(new ItemStack(ItemLoader.iron_dust), new ItemStack(ItemLoader.gold_dust),
				new ItemStack(ItemLoader.quartz_dust), new ItemStack(ItemLoader.etherium), 200));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_iron_ore),
				new ItemStack(ItemLoader.tiny_pile_iron_dust), new ItemStack(ItemLoader.tiny_pile_iron_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_gold_ore),
				new ItemStack(ItemLoader.tiny_pile_gold_dust), new ItemStack(ItemLoader.tiny_pile_gold_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_diamond_ore),
				new ItemStack(ItemLoader.tiny_pile_diamond_dust), new ItemStack(ItemLoader.tiny_pile_diamond_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_emerald_ore),
				new ItemStack(ItemLoader.tiny_pile_emerald_dust), new ItemStack(ItemLoader.tiny_pile_emerald_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_redstone_ore),
				new ItemStack(ItemLoader.small_pile_redstone_dust), new ItemStack(ItemLoader.small_pile_redstone_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_coal_ore),
				new ItemStack(ItemLoader.small_pile_coal_dust), new ItemStack(ItemLoader.small_pile_coal_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_lapis_ore),
				new ItemStack(ItemLoader.small_pile_lapis_dust), new ItemStack(ItemLoader.small_pile_lapis_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
		register(new RecipeCentrifuge(new ItemStack(ItemLoader.crushed_quartz_ore),
				new ItemStack(ItemLoader.small_pile_quartz_dust), new ItemStack(ItemLoader.small_pile_quartz_dust),
				new ItemStack(ItemLoader.small_pile_stone_dust), 100));
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
		GRINDER, ALLOY_SMELTER, CENTRIFUGE;
	}
}
