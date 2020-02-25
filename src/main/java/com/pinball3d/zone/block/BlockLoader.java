package com.pinball3d.zone.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class BlockLoader {
	public static Block iron_hull, clarity_glass, reinforced_glass, etherium_hull, etherium_bars, clarity_glass_pane;

	public static Block drainer, grinder, grinder_light, elec_furnace, elec_furnace_light, alloy_smelter,
			alloy_smelter_light;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		register(registry, iron_hull = new BlockIronHull());
		register(registry, clarity_glass = new BlockClarityGlass());
		register(registry, reinforced_glass = new BlockReinforcedGlass());
		register(registry, etherium_hull = new BlockEtheriumHull());
		register(registry, etherium_bars = new BlockEtheriumBars());
		register(registry, clarity_glass_pane = new BlockClarityGlassPane());
		register(registry, drainer = new BlockDrainer());
		register(registry, grinder = new BlockGrinder());
		register(registry, elec_furnace = new BlockElecFurnace());
		register(registry, alloy_smelter = new BlockAlloySmelter(false));
		register(registry, alloy_smelter_light = new BlockAlloySmelter(true));
	}

	private static void register(IForgeRegistry<Block> registry, Block block) {
		registry.register(block);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registerItem(registry, iron_hull);
		registerItem(registry, clarity_glass);
		registerItem(registry, reinforced_glass);
		registerItem(registry, etherium_hull);
		registerItem(registry, etherium_bars);
		registerItem(registry, clarity_glass_pane);
		registerItem(registry, drainer);
		registerItem(registry, grinder);
		registerItem(registry, elec_furnace);
		registerItem(registry, alloy_smelter);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Block block) {
		Item item = new ItemBlock(block).setRegistryName(block.getRegistryName());
		registry.register(item);
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerRender(item);
		}

	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item) {
		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, model);
	}
}
