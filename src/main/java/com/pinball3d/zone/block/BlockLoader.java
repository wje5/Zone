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
	public static Block iron_hull, clarity_glass, reinforced_glass, etherium_hull, etherium_bars, clarity_glass_pane,
			etherium_frame, structure_block, reinforced_glass_pane, charged_glass, charged_glass_pane;

	public static Block drainer, grinder, grinder_light, elec_furnace, elec_furnace_light, alloy_smelter,
			alloy_smelter_light, centrifuge, centrifuge_light, node, crystallizer, crystallizer_light;

	public static Block cluster_operation_module, controller_mainframe, processing_center, truss;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		register(registry, iron_hull = new BlockIronHull());
		register(registry, clarity_glass = new BlockClarityGlass());
		register(registry, reinforced_glass = new BlockReinforcedGlass());
		register(registry, etherium_hull = new BlockEtheriumHull());
		register(registry, etherium_bars = new BlockEtheriumBars());
		register(registry, clarity_glass_pane = new BlockClarityGlassPane());
		register(registry, etherium_frame = new BlockEtheriumFrame());
		register(registry, structure_block = new BlockStructureBlock());
		register(registry, reinforced_glass_pane = new BlockReinforcedGlassPane());
		register(registry, charged_glass = new BlockChargedGlass());
		register(registry, charged_glass_pane = new BlockChargedGlassPane());
		register(registry, drainer = new BlockDrainer());
		register(registry, grinder = new BlockGrinder(false));
		register(registry, grinder_light = new BlockGrinder(true));
		register(registry, elec_furnace = new BlockElecFurnace(false));
		register(registry, elec_furnace_light = new BlockElecFurnace(true));
		register(registry, alloy_smelter = new BlockAlloySmelter(false));
		register(registry, alloy_smelter_light = new BlockAlloySmelter(true));
		register(registry, centrifuge = new BlockCentrifuge(false));
		register(registry, centrifuge_light = new BlockCentrifuge(true));
		register(registry, node = new BlockNode());
		register(registry, crystallizer = new BlockCrystallizer(false));
		register(registry, crystallizer_light = new BlockCrystallizer(true));
		register(registry, cluster_operation_module = new BlockClusterOperationModule());
		register(registry, controller_mainframe = new BlockControllerMainframe());
		register(registry, processing_center = new BlockProcessingCenter());
		register(registry, truss = new BlockTruss());
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
		registerItem(registry, etherium_frame);
		registerItem(registry, structure_block);
		registerItem(registry, reinforced_glass_pane);
		registerItem(registry, charged_glass);
		registerItem(registry, charged_glass_pane);
		registerItem(registry, drainer);
		registerItem(registry, grinder);
		registerItem(registry, elec_furnace);
		registerItem(registry, alloy_smelter);
		registerItem(registry, centrifuge);
		registerItem(registry, node);
		registerItem(registry, crystallizer);
		registerItem(registry, cluster_operation_module);
		registerItem(registry, controller_mainframe);
		registerItem(registry, processing_center);
		registerItem(registry, truss);
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
