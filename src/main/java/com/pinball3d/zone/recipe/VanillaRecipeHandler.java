package com.pinball3d.zone.recipe;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class VanillaRecipeHandler {
	public static void init() {
		GameRegistry.addSmelting(new ItemStack(ItemLoader.crushed_iron_ore), new ItemStack(Items.IRON_NUGGET), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.crushed_gold_ore), new ItemStack(Items.GOLD_NUGGET), 0.15F);
		GameRegistry.addSmelting(new ItemStack(Blocks.SOUL_SAND), new ItemStack(BlockLoader.clarity_glass), 0.2F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.iron_dust), new ItemStack(Items.IRON_INGOT), 0F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.gold_dust), new ItemStack(Items.GOLD_INGOT), 0F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.tiny_pile_iron_dust), new ItemStack(Items.IRON_NUGGET), 0F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.tiny_pile_gold_dust), new ItemStack(Items.GOLD_NUGGET), 0F);
		GameRegistry.addSmelting(new ItemStack(ItemLoader.etherium_dust), new ItemStack(ItemLoader.etherium), 0F);
	}
}
