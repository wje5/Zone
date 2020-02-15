package com.pinball3d.zone.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class BlockLoader {
	public static Block iron_hull;

	public static Block drainer, grinder, elec_furnace;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		register(registry, iron_hull = new BlockIronHull());
		register(registry, drainer = new BlockDrainer());
		register(registry, grinder = new BlockGrinder());
		register(registry, elec_furnace = new BlockElecFurnace());
	}

	private static void register(IForgeRegistry<Block> registry, Block block) {
		registry.register(block);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registerItem(registry, iron_hull);
		registerItem(registry, drainer);
		registerItem(registry, grinder);
		registerItem(registry, elec_furnace);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Block block) {
		Item item = new ItemBlock(block).setRegistryName(block.getRegistryName());
		registry.register(item);
		registerRender(item);

	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item) {
		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, model);
	}
}
