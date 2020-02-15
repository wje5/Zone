package com.pinball3d.zone.recipe;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class VanillaRecipeHandler {
	public static void init() {
		GameRegistry.addSmelting(new ItemStack(ItemLoader.crushed_iron_ore), new ItemStack(Items.IRON_NUGGET), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.crushed_gold_ore), new ItemStack(Items.GOLD_NUGGET), 0.15F);
	}
}
