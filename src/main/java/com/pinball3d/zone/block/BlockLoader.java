package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.item.ItemCastingTable;
import com.pinball3d.zone.item.ItemConstructBlock;
import com.pinball3d.zone.item.ItemCrucible;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
	public static Block iron_hull, clarity_glass, reinforced_glass, etherium_hull, etherium_block, etherium_bars,
			clarity_glass_pane, etherium_frame, reinforced_glass_pane, charged_glass, charged_glass_pane, firm_glass,
			firm_glass_pane, reinforced_stone;

	public static Block drainer, grinder, grinder_light, elec_furnace, elec_furnace_light, alloy_smelter,
			alloy_smelter_light, centrifuge, centrifuge_light, node, crystallizer, crystallizer_light, crucible,
			burning_box, burning_box_light, casting_table, boiler, boiler_light, lathe, lathe_light, forming_press,
			forming_press_light, pump, pump_light;

	public static Block construct_block, construct_block_all, cluster_operation_module, controller_mainframe,
			processing_center, processing_center_light, truss, dynavolt_restrainer, transmission_module, storage_panel,
			storage_chest, io_panel, production_panel, beacon_amplifier_matrix, beacon_core, piano;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		register(registry,
				iron_hull = setName(new Block(Material.IRON).setHardness(5.0F).setResistance(10.0F), "iron_hull"));
		register(registry, clarity_glass = new BlockClarityGlass());
		register(registry, reinforced_glass = new BlockReinforcedGlass());
		register(registry, etherium_hull = new BlockEtheriumHull());
		register(registry, etherium_block = new BlockEtheriumBlock());
		register(registry, etherium_bars = new BlockEtheriumBars());
		register(registry, clarity_glass_pane = new BlockClarityGlassPane());
		register(registry, etherium_frame = new BlockEtheriumFrame());
		register(registry, reinforced_glass_pane = new BlockReinforcedGlassPane());
		register(registry, charged_glass = new BlockChargedGlass());
		register(registry, charged_glass_pane = new BlockChargedGlassPane());
		register(registry, firm_glass = new BlockFirmGlass());
		register(registry, firm_glass_pane = new BlockFirmGlassPane());
		register(registry, reinforced_stone = new BlockReinforcedStone());
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
		register(registry, crucible = new BlockCrucible());
		register(registry, burning_box = new BlockBurningBox(false));
		register(registry, burning_box_light = new BlockBurningBox(true));
		register(registry, casting_table = new BlockCastingTable());
		register(registry, boiler = new BlockBoiler(false));
		register(registry, boiler_light = new BlockBoiler(true));
		register(registry, lathe = new BlockLathe(false));
		register(registry, lathe_light = new BlockLathe(true));
		register(registry, forming_press = new BlockFormingPress(false));
		register(registry, forming_press_light = new BlockFormingPress(true));
		register(registry, pump = new BlockPump(false));
		register(registry, pump_light = new BlockPump(true));
		register(registry, construct_block = new BlockConstructBlock());
		register(registry, construct_block_all = new BlockConstructBlockAll());
		register(registry, cluster_operation_module = new BlockClusterOperationModule());
		register(registry, controller_mainframe = new BlockControllerMainframe());
		register(registry, processing_center = new BlockProcessingCenter(false));
		register(registry, processing_center_light = new BlockProcessingCenter(true));
		register(registry, truss = new BlockTruss());
		register(registry, dynavolt_restrainer = new BlockDynavoltRestrainer());
		register(registry, transmission_module = new BlockTransmissionModule());
		register(registry, storage_panel = new BlockStoragePanel());
		register(registry, storage_chest = new BlockStorageChest());
		register(registry, io_panel = new BlockIOPanel());
		register(registry, production_panel = new BlockProductionPanel());
		register(registry, beacon_amplifier_matrix = new BlockBeaconAmplifierMatrix());
		register(registry, beacon_core = new BlockBeaconCore());
		register(registry, piano = new BlockPiano());
	}

	private static Block setName(Block block, String name) {
		return block.setRegistryName("zone", name).setUnlocalizedName(name).setCreativeTab(TabZone.tab);
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
		registerItem(registry, etherium_block);
		registerItem(registry, etherium_bars);
		registerItem(registry, clarity_glass_pane);
		registerItem(registry, etherium_frame);
		registerItem(registry, reinforced_glass_pane);
		registerItem(registry, charged_glass);
		registerItem(registry, charged_glass_pane);
		registerItem(registry, firm_glass);
		registerItem(registry, firm_glass_pane);
		registerItem(registry, reinforced_stone);
		registerItem(registry, drainer);
		registerItem(registry, grinder);
		registerItem(registry, elec_furnace);
		registerItem(registry, alloy_smelter);
		registerItem(registry, centrifuge);
		registerItem(registry, node);
		registerItem(registry, crystallizer);
		registerItem(registry, ItemLoader.crucible = new ItemCrucible());
		registerItem(registry, burning_box);
		registerItem(registry, ItemLoader.casting_table = new ItemCastingTable());
		registerItem(registry, boiler);
		registerItem(registry, lathe);
		registerItem(registry, forming_press);
		registerItem(registry, pump);
		registerItem(registry, ItemLoader.construct_block = new ItemConstructBlock());
		registerItem(registry, cluster_operation_module);
		registerItem(registry, controller_mainframe);
		registerItem(registry, processing_center);
		registerItem(registry, truss);
		registerItem(registry, dynavolt_restrainer);
		registerItem(registry, transmission_module);
		registerItem(registry, storage_panel);
		registerItem(registry, storage_chest);
		registerItem(registry, io_panel);
		registerItem(registry, production_panel);
		registerItem(registry, beacon_amplifier_matrix);
		registerItem(registry, beacon_core);
		registerItem(registry, piano);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Block block) {
		registerItem(registry, new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item) {
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
