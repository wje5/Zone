package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.item.ItemConstructBlock;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEAlloySmelter;
import com.pinball3d.zone.tileentity.TEBoiler;
import com.pinball3d.zone.tileentity.TECapacitor;
import com.pinball3d.zone.tileentity.TECentrifuge;
import com.pinball3d.zone.tileentity.TECharger;
import com.pinball3d.zone.tileentity.TECrystallizer;
import com.pinball3d.zone.tileentity.TEDrainer;
import com.pinball3d.zone.tileentity.TEElecFurnace;
import com.pinball3d.zone.tileentity.TEExtruder;
import com.pinball3d.zone.tileentity.TEFormingPress;
import com.pinball3d.zone.tileentity.TEGrinder;
import com.pinball3d.zone.tileentity.TEMiner;
import com.pinball3d.zone.tileentity.TEPump;
import com.pinball3d.zone.tileentity.ZoneTieredMachine.Tier;

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

	public static Block node, crucible, burning_box, burning_box_light, casting_table;

	public static Block drainer_1, drainer_2, drainer_3, boiler_1, boiler_1_light, boiler_2, boiler_2_light, boiler_3,
			boiler_3_light, capacitor_1, capacitor_2, capacitor_3, alloy_smelter_1, alloy_smelter_1_light,
			alloy_smelter_2, alloy_smelter_2_light, alloy_smelter_3, alloy_smelter_3_light, centrifuge_1,
			centrifuge_1_light, centrifuge_2, centrifuge_2_light, centrifuge_3, centrifuge_3_light, charger_1,
			charger_1_light, charger_2, charger_2_light, charger_3, charger_3_light, crystallizer_1,
			crystallizer_1_light, crystallizer_2, crystallizer_2_light, crystallizer_3, crystallizer_3_light,
			elec_furnace_1, elec_furnace_1_light, elec_furnace_2, elec_furnace_2_light, elec_furnace_3,
			elec_furnace_3_light, extruder_1, extruder_1_light, extruder_2, extruder_2_light, extruder_3,
			extruder_3_light, forming_press_1, forming_press_1_light, forming_press_2, forming_press_2_light,
			forming_press_3, forming_press_3_light, grinder_1, grinder_1_light, grinder_2, grinder_2_light, grinder_3,
			grinder_3_light, pump_1, pump_1_light, pump_2, pump_2_light, pump_3, pump_3_light, miner_1, miner_1_light,
			miner_2, miner_2_light, miner_3, miner_3_light;

	public static Block construct_block, construct_block_all, cluster_operation_module, controller_mainframe,
			processing_center, processing_center_light, truss, dynavolt_restrainer, transmission_module,
			beacon_amplifier_matrix, beacon_core, powered_piston, powered_piston_sticky, powered_piston_moving,
			powered_piston_head, terminal, cable_1;

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

		register(registry, node = new BlockNode());

		register(registry, drainer_1 = new BlockTieredMachine("drainer", GuiElementLoader.DRAINER,
				t -> new TEDrainer(t), Tier.T1));
		register(registry, drainer_2 = new BlockTieredMachine("drainer", GuiElementLoader.DRAINER,
				t -> new TEDrainer(t), Tier.T2));
		register(registry, drainer_3 = new BlockTieredMachine("drainer", GuiElementLoader.DRAINER,
				t -> new TEDrainer(t), Tier.T3));

		register(registry, boiler_1 = new BlockTieredMachineLightable("boiler", GuiElementLoader.BOILER,
				t -> new TEBoiler(t), Tier.T1, false, () -> boiler_1, () -> boiler_1_light));
		register(registry, boiler_1_light = new BlockTieredMachineLightable("boiler", GuiElementLoader.BOILER,
				t -> new TEBoiler(t), Tier.T1, true, () -> boiler_1, () -> boiler_1_light));
		register(registry, boiler_2 = new BlockTieredMachineLightable("boiler", GuiElementLoader.BOILER,
				t -> new TEBoiler(t), Tier.T2, false, () -> boiler_2, () -> boiler_2_light));
		register(registry, boiler_2_light = new BlockTieredMachineLightable("boiler", GuiElementLoader.BOILER,
				t -> new TEBoiler(t), Tier.T2, true, () -> boiler_2, () -> boiler_2_light));
		register(registry, boiler_3 = new BlockTieredMachineLightable("boiler", GuiElementLoader.BOILER,
				t -> new TEBoiler(t), Tier.T3, false, () -> boiler_3, () -> boiler_3_light));
		register(registry, boiler_3_light = new BlockTieredMachineLightable("boiler", GuiElementLoader.BOILER,
				t -> new TEBoiler(t), Tier.T3, true, () -> boiler_3, () -> boiler_3_light));

		register(registry, capacitor_1 = new BlockTieredMachine("capacitor", GuiElementLoader.CAPACITOR,
				t -> new TECapacitor(t), Tier.T1));
		register(registry, capacitor_2 = new BlockTieredMachine("capacitor", GuiElementLoader.CAPACITOR,
				t -> new TECapacitor(t), Tier.T2));
		register(registry, capacitor_3 = new BlockTieredMachine("capacitor", GuiElementLoader.CAPACITOR,
				t -> new TECapacitor(t), Tier.T3));

		register(registry,
				alloy_smelter_1 = new BlockTieredMachineLightable("alloy_smelter", GuiElementLoader.ALLOY_SMELTER,
						t -> new TEAlloySmelter(t), Tier.T1, false, () -> alloy_smelter_1,
						() -> alloy_smelter_1_light));
		register(registry,
				alloy_smelter_1_light = new BlockTieredMachineLightable("alloy_smelter", GuiElementLoader.ALLOY_SMELTER,
						t -> new TEAlloySmelter(t), Tier.T1, true, () -> alloy_smelter_1, () -> alloy_smelter_1_light));
		register(registry,
				alloy_smelter_2 = new BlockTieredMachineLightable("alloy_smelter", GuiElementLoader.ALLOY_SMELTER,
						t -> new TEAlloySmelter(t), Tier.T2, false, () -> alloy_smelter_2,
						() -> alloy_smelter_2_light));
		register(registry,
				alloy_smelter_2_light = new BlockTieredMachineLightable("alloy_smelter", GuiElementLoader.ALLOY_SMELTER,
						t -> new TEAlloySmelter(t), Tier.T2, true, () -> alloy_smelter_2, () -> alloy_smelter_2_light));
		register(registry,
				alloy_smelter_3 = new BlockTieredMachineLightable("alloy_smelter", GuiElementLoader.ALLOY_SMELTER,
						t -> new TEAlloySmelter(t), Tier.T3, false, () -> alloy_smelter_3,
						() -> alloy_smelter_3_light));
		register(registry,
				alloy_smelter_3_light = new BlockTieredMachineLightable("alloy_smelter", GuiElementLoader.ALLOY_SMELTER,
						t -> new TEAlloySmelter(t), Tier.T3, true, () -> alloy_smelter_3, () -> alloy_smelter_3_light));

		register(registry, centrifuge_1 = new BlockTieredMachineLightable("centrifuge", GuiElementLoader.CENTRIFUGE,
				t -> new TECentrifuge(t), Tier.T1, false, () -> centrifuge_1, () -> centrifuge_1_light));
		register(registry,
				centrifuge_1_light = new BlockTieredMachineLightable("centrifuge", GuiElementLoader.CENTRIFUGE,
						t -> new TECentrifuge(t), Tier.T1, true, () -> centrifuge_1, () -> centrifuge_1_light));
		register(registry, centrifuge_2 = new BlockTieredMachineLightable("centrifuge", GuiElementLoader.CENTRIFUGE,
				t -> new TECentrifuge(t), Tier.T2, false, () -> centrifuge_2, () -> centrifuge_2_light));
		register(registry,
				centrifuge_2_light = new BlockTieredMachineLightable("centrifuge", GuiElementLoader.CENTRIFUGE,
						t -> new TECentrifuge(t), Tier.T2, true, () -> centrifuge_2, () -> centrifuge_2_light));
		register(registry, centrifuge_3 = new BlockTieredMachineLightable("centrifuge", GuiElementLoader.CENTRIFUGE,
				t -> new TECentrifuge(t), Tier.T3, false, () -> centrifuge_3, () -> centrifuge_3_light));
		register(registry,
				centrifuge_3_light = new BlockTieredMachineLightable("centrifuge", GuiElementLoader.CENTRIFUGE,
						t -> new TECentrifuge(t), Tier.T3, true, () -> centrifuge_3, () -> centrifuge_3_light));

		register(registry, charger_1 = new BlockTieredMachineLightable("charger", GuiElementLoader.CHARGER,
				t -> new TECharger(t), Tier.T1, false, () -> charger_1, () -> charger_1_light));
		register(registry, charger_1_light = new BlockTieredMachineLightable("charger", GuiElementLoader.CHARGER,
				t -> new TECharger(t), Tier.T1, true, () -> charger_1, () -> charger_1_light));
		register(registry, charger_2 = new BlockTieredMachineLightable("charger", GuiElementLoader.CHARGER,
				t -> new TECharger(t), Tier.T2, false, () -> charger_2, () -> charger_2_light));
		register(registry, charger_2_light = new BlockTieredMachineLightable("charger", GuiElementLoader.CHARGER,
				t -> new TECharger(t), Tier.T2, true, () -> charger_2, () -> charger_2_light));
		register(registry, charger_3 = new BlockTieredMachineLightable("charger", GuiElementLoader.CHARGER,
				t -> new TECharger(t), Tier.T3, false, () -> charger_3, () -> charger_3_light));
		register(registry, charger_3_light = new BlockTieredMachineLightable("charger", GuiElementLoader.CHARGER,
				t -> new TECharger(t), Tier.T3, true, () -> charger_3, () -> charger_3_light));

		register(registry,
				crystallizer_1 = new BlockTieredMachineLightable("crystallizer", GuiElementLoader.CRYSTALLIZER,
						t -> new TECrystallizer(t), Tier.T1, false, () -> crystallizer_1, () -> crystallizer_1_light));
		register(registry,
				crystallizer_1_light = new BlockTieredMachineLightable("crystallizer", GuiElementLoader.CRYSTALLIZER,
						t -> new TECrystallizer(t), Tier.T1, true, () -> crystallizer_1, () -> crystallizer_1_light));
		register(registry,
				crystallizer_2 = new BlockTieredMachineLightable("crystallizer", GuiElementLoader.CRYSTALLIZER,
						t -> new TECrystallizer(t), Tier.T2, false, () -> crystallizer_2, () -> crystallizer_2_light));
		register(registry,
				crystallizer_2_light = new BlockTieredMachineLightable("crystallizer", GuiElementLoader.CRYSTALLIZER,
						t -> new TECrystallizer(t), Tier.T2, true, () -> crystallizer_2, () -> crystallizer_2_light));
		register(registry,
				crystallizer_3 = new BlockTieredMachineLightable("crystallizer", GuiElementLoader.CRYSTALLIZER,
						t -> new TECrystallizer(t), Tier.T3, false, () -> crystallizer_3, () -> crystallizer_3_light));
		register(registry,
				crystallizer_3_light = new BlockTieredMachineLightable("crystallizer", GuiElementLoader.CRYSTALLIZER,
						t -> new TECrystallizer(t), Tier.T3, true, () -> crystallizer_3, () -> crystallizer_3_light));

		register(registry,
				elec_furnace_1 = new BlockTieredMachineLightable("elec_furnace", GuiElementLoader.ELEC_FURNACE,
						t -> new TEElecFurnace(t), Tier.T1, false, () -> elec_furnace_1, () -> elec_furnace_1_light));
		register(registry,
				elec_furnace_1_light = new BlockTieredMachineLightable("elec_furnace", GuiElementLoader.ELEC_FURNACE,
						t -> new TEElecFurnace(t), Tier.T1, true, () -> elec_furnace_1, () -> elec_furnace_1_light));
		register(registry,
				elec_furnace_2 = new BlockTieredMachineLightable("elec_furnace", GuiElementLoader.ELEC_FURNACE,
						t -> new TEElecFurnace(t), Tier.T2, false, () -> elec_furnace_2, () -> elec_furnace_2_light));
		register(registry,
				elec_furnace_2_light = new BlockTieredMachineLightable("elec_furnace", GuiElementLoader.ELEC_FURNACE,
						t -> new TEElecFurnace(t), Tier.T2, true, () -> elec_furnace_2, () -> elec_furnace_2_light));
		register(registry,
				elec_furnace_3 = new BlockTieredMachineLightable("elec_furnace", GuiElementLoader.ELEC_FURNACE,
						t -> new TEElecFurnace(t), Tier.T3, false, () -> elec_furnace_3, () -> elec_furnace_3_light));
		register(registry,
				elec_furnace_3_light = new BlockTieredMachineLightable("elec_furnace", GuiElementLoader.ELEC_FURNACE,
						t -> new TEElecFurnace(t), Tier.T3, true, () -> elec_furnace_3, () -> elec_furnace_3_light));

		register(registry, extruder_1 = new BlockTieredMachineLightable("extruder", GuiElementLoader.EXTRUDER,
				t -> new TEExtruder(t), Tier.T1, false, () -> extruder_1, () -> extruder_1_light));
		register(registry, extruder_1_light = new BlockTieredMachineLightable("extruder", GuiElementLoader.EXTRUDER,
				t -> new TEExtruder(t), Tier.T1, true, () -> extruder_1, () -> extruder_1_light));
		register(registry, extruder_2 = new BlockTieredMachineLightable("extruder", GuiElementLoader.EXTRUDER,
				t -> new TEExtruder(t), Tier.T2, false, () -> extruder_2, () -> extruder_2_light));
		register(registry, extruder_2_light = new BlockTieredMachineLightable("extruder", GuiElementLoader.EXTRUDER,
				t -> new TEExtruder(t), Tier.T2, true, () -> extruder_2, () -> extruder_2_light));
		register(registry, extruder_3 = new BlockTieredMachineLightable("extruder", GuiElementLoader.EXTRUDER,
				t -> new TEExtruder(t), Tier.T3, false, () -> extruder_3, () -> extruder_3_light));
		register(registry, extruder_3_light = new BlockTieredMachineLightable("extruder", GuiElementLoader.EXTRUDER,
				t -> new TEExtruder(t), Tier.T3, true, () -> extruder_3, () -> extruder_3_light));

		register(registry,
				forming_press_1 = new BlockTieredMachineLightable("forming_press", GuiElementLoader.FORMING_PRESS,
						t -> new TEFormingPress(t), Tier.T1, false, () -> forming_press_1,
						() -> forming_press_1_light));
		register(registry,
				forming_press_1_light = new BlockTieredMachineLightable("forming_press", GuiElementLoader.FORMING_PRESS,
						t -> new TEFormingPress(t), Tier.T1, true, () -> forming_press_1, () -> forming_press_1_light));
		register(registry,
				forming_press_2 = new BlockTieredMachineLightable("forming_press", GuiElementLoader.FORMING_PRESS,
						t -> new TEFormingPress(t), Tier.T2, false, () -> forming_press_2,
						() -> forming_press_2_light));
		register(registry,
				forming_press_2_light = new BlockTieredMachineLightable("forming_press", GuiElementLoader.FORMING_PRESS,
						t -> new TEFormingPress(t), Tier.T2, true, () -> forming_press_2, () -> forming_press_2_light));
		register(registry,
				forming_press_3 = new BlockTieredMachineLightable("forming_press", GuiElementLoader.FORMING_PRESS,
						t -> new TEFormingPress(t), Tier.T3, false, () -> forming_press_3,
						() -> forming_press_3_light));
		register(registry,
				forming_press_3_light = new BlockTieredMachineLightable("forming_press", GuiElementLoader.FORMING_PRESS,
						t -> new TEFormingPress(t), Tier.T3, true, () -> forming_press_3, () -> forming_press_3_light));

		register(registry, grinder_1 = new BlockTieredMachineLightable("grinder", GuiElementLoader.GRINDER,
				t -> new TEGrinder(t), Tier.T1, false, () -> grinder_1, () -> grinder_1_light));
		register(registry, grinder_1_light = new BlockTieredMachineLightable("grinder", GuiElementLoader.GRINDER,
				t -> new TEGrinder(t), Tier.T1, true, () -> grinder_1, () -> grinder_1_light));
		register(registry, grinder_2 = new BlockTieredMachineLightable("grinder", GuiElementLoader.GRINDER,
				t -> new TEGrinder(t), Tier.T2, false, () -> grinder_2, () -> grinder_2_light));
		register(registry, grinder_2_light = new BlockTieredMachineLightable("grinder", GuiElementLoader.GRINDER,
				t -> new TEGrinder(t), Tier.T2, true, () -> grinder_2, () -> grinder_2_light));
		register(registry, grinder_3 = new BlockTieredMachineLightable("grinder", GuiElementLoader.GRINDER,
				t -> new TEGrinder(t), Tier.T3, false, () -> grinder_3, () -> grinder_3_light));
		register(registry, grinder_3_light = new BlockTieredMachineLightable("grinder", GuiElementLoader.GRINDER,
				t -> new TEGrinder(t), Tier.T3, true, () -> grinder_3, () -> grinder_3_light));

		register(registry, pump_1 = new BlockTieredMachineLightable("pump", GuiElementLoader.PUMP, t -> new TEPump(t),
				Tier.T1, false, () -> pump_1, () -> pump_1_light));
		register(registry, pump_1_light = new BlockTieredMachineLightable("pump", GuiElementLoader.PUMP,
				t -> new TEPump(t), Tier.T1, true, () -> pump_1, () -> pump_1_light));
		register(registry, pump_2 = new BlockTieredMachineLightable("pump", GuiElementLoader.PUMP, t -> new TEPump(t),
				Tier.T2, false, () -> pump_2, () -> pump_2_light));
		register(registry, pump_2_light = new BlockTieredMachineLightable("pump", GuiElementLoader.PUMP,
				t -> new TEPump(t), Tier.T2, true, () -> pump_2, () -> pump_2_light));
		register(registry, pump_3 = new BlockTieredMachineLightable("pump", GuiElementLoader.PUMP, t -> new TEPump(t),
				Tier.T3, false, () -> pump_3, () -> pump_3_light));
		register(registry, pump_3_light = new BlockTieredMachineLightable("pump", GuiElementLoader.PUMP,
				t -> new TEPump(t), Tier.T3, true, () -> pump_3, () -> pump_3_light));

		register(registry, miner_1 = new BlockTieredMachineLightable("miner", GuiElementLoader.MINER,
				t -> new TEMiner(t), Tier.T1, false, () -> miner_1, () -> miner_1_light));
		register(registry, miner_1_light = new BlockTieredMachineLightable("miner", GuiElementLoader.MINER,
				t -> new TEMiner(t), Tier.T1, true, () -> miner_1, () -> miner_1_light));
		register(registry, miner_2 = new BlockTieredMachineLightable("miner", GuiElementLoader.MINER,
				t -> new TEMiner(t), Tier.T2, false, () -> miner_2, () -> miner_2_light));
		register(registry, miner_2_light = new BlockTieredMachineLightable("miner", GuiElementLoader.MINER,
				t -> new TEMiner(t), Tier.T2, true, () -> miner_2, () -> miner_2_light));
		register(registry, miner_3 = new BlockTieredMachineLightable("miner", GuiElementLoader.MINER,
				t -> new TEMiner(t), Tier.T3, false, () -> miner_3, () -> miner_3_light));
		register(registry, miner_3_light = new BlockTieredMachineLightable("miner", GuiElementLoader.MINER,
				t -> new TEMiner(t), Tier.T3, true, () -> miner_3, () -> miner_3_light));

		register(registry, construct_block = new BlockConstructBlock());
		register(registry, construct_block_all = new BlockConstructBlockAll());
		register(registry, cluster_operation_module = new BlockClusterOperationModule());
		register(registry, controller_mainframe = new BlockControllerMainframe());
		register(registry, processing_center = new BlockProcessingCenter(false));
		register(registry, processing_center_light = new BlockProcessingCenter(true));
		register(registry, truss = new BlockTruss());
		register(registry, dynavolt_restrainer = new BlockDynavoltRestrainer());
		register(registry, transmission_module = new BlockTransmissionModule());
		register(registry, beacon_amplifier_matrix = new BlockBeaconAmplifierMatrix());
		register(registry, beacon_core = new BlockBeaconCore());
		register(registry, powered_piston = new BlockPoweredPiston(false));
		register(registry, powered_piston_sticky = new BlockPoweredPiston(true));
		register(registry, powered_piston_moving = new BlockPoweredPistonMoving());
		register(registry, powered_piston_head = new BlockPoweredPistonHead());
		register(registry, terminal = new BlockTerminal());
		register(registry, cable_1 = new BlockCableBasic());
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

		registerItem(registry, node);

		registerItem(registry, drainer_1);
		registerItem(registry, drainer_2);
		registerItem(registry, drainer_3);
		registerItem(registry, boiler_1);
		registerItem(registry, boiler_2);
		registerItem(registry, boiler_3);
		registerItem(registry, capacitor_1);
		registerItem(registry, capacitor_2);
		registerItem(registry, capacitor_3);
		registerItem(registry, alloy_smelter_1);
		registerItem(registry, alloy_smelter_2);
		registerItem(registry, alloy_smelter_3);
		registerItem(registry, centrifuge_1);
		registerItem(registry, centrifuge_2);
		registerItem(registry, centrifuge_3);
		registerItem(registry, charger_1);
		registerItem(registry, charger_2);
		registerItem(registry, charger_3);
		registerItem(registry, crystallizer_1);
		registerItem(registry, crystallizer_2);
		registerItem(registry, crystallizer_3);
		registerItem(registry, elec_furnace_1);
		registerItem(registry, elec_furnace_2);
		registerItem(registry, elec_furnace_3);
		registerItem(registry, extruder_1);
		registerItem(registry, extruder_2);
		registerItem(registry, extruder_3);
		registerItem(registry, forming_press_1);
		registerItem(registry, forming_press_2);
		registerItem(registry, forming_press_3);
		registerItem(registry, grinder_1);
		registerItem(registry, grinder_2);
		registerItem(registry, grinder_3);
		registerItem(registry, pump_1);
		registerItem(registry, pump_2);
		registerItem(registry, pump_3);
		registerItem(registry, miner_1);
		registerItem(registry, miner_2);
		registerItem(registry, miner_3);

		registerItem(registry, ItemLoader.construct_block = new ItemConstructBlock());
		registerItem(registry, cluster_operation_module);
		registerItem(registry, controller_mainframe);
		registerItem(registry, processing_center);
		registerItem(registry, truss);
		registerItem(registry, dynavolt_restrainer);
		registerItem(registry, transmission_module);
		registerItem(registry, beacon_amplifier_matrix);
		registerItem(registry, beacon_core);
		registerItem(registry, powered_piston);
		registerItem(registry, powered_piston_sticky);
		registerItem(registry, terminal);
		registerItem(registry, cable_1);
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
