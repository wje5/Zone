package com.pinball3d.zone.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class ItemLoader {
	public static Item hammer, spanner, file, saw;

	public static Item iron_plate, rivet, iron_plate_riveted, quartz_circuit_board, vacuum_tube, chip, circuit_board,
			grind_head, gold_plate, gold_plate_riveted, thermal_amplifier, etherium, etherium_plate,
			etherium_plate_riveted, kinetic_amplifier, etherium_rod, advenced_circuit_board, processing_unit,
			transistor, energy_group, restrain_circle, theile_tube, laser_generater;

	public static Item energy;

	public static Item crushed_iron_ore, crushed_gold_ore, crushed_diamond_ore, crushed_emerald_ore,
			crushed_redstone_ore, crushed_coal_ore, crushed_lapis_ore;

	public static Item small_pile_redstone_dust, small_pile_coal_dust, small_pile_lapis_dust;

	public static Item tiny_pile_iron_dust, tiny_pile_gold_dust, tiny_pile_diamond_dust, tiny_pile_emerald_dust;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		register(registry, hammer = new ItemCraftTool("hammer"));
		register(registry, spanner = new ItemCraftTool("spanner"));
		register(registry, file = new ItemCraftTool("file"));
		register(registry, saw = new ItemCraftTool("saw"));
		register(registry, iron_plate = new ZoneItem("iron_plate"));
		register(registry, rivet = new ZoneItem("rivet"));
		register(registry, iron_plate_riveted = new ZoneItem("iron_plate_riveted"));
		register(registry, quartz_circuit_board = new ZoneItem("quartz_circuit_board"));
		register(registry, vacuum_tube = new ZoneItem("vacuum_tube"));
		register(registry, chip = new ZoneItem("chip"));
		register(registry, circuit_board = new ZoneItem("circuit_board"));
		register(registry, grind_head = new ZoneItem("grind_head"));
		register(registry, gold_plate = new ZoneItem("gold_plate"));
		register(registry, gold_plate_riveted = new ZoneItem("gold_plate_riveted"));
		register(registry, thermal_amplifier = new ZoneItem("thermal_amplifier"));
		register(registry, etherium = new ZoneItem("etherium"));
		register(registry, etherium_plate = new ZoneItem("etherium_plate"));
		register(registry, etherium_plate_riveted = new ZoneItem("etherium_plate_riveted"));
		register(registry, kinetic_amplifier = new ZoneItem("kinetic_amplifier"));
		register(registry, etherium_rod = new ZoneItem("etherium_rod"));
		register(registry, advenced_circuit_board = new ZoneItem("advenced_circuit_board"));
		register(registry, processing_unit = new ZoneItem("processing_unit"));
		register(registry, transistor = new ZoneItem("transistor"));
		register(registry, energy_group = new ZoneItem("energy_group"));
		register(registry, restrain_circle = new ZoneItem("restrain_circle"));
		register(registry, theile_tube = new ZoneItem("theile_tube"));
		register(registry, laser_generater = new ZoneItem("laser_generater"));
		register(registry, energy = new ZoneItem("energy"));
		register(registry, crushed_iron_ore = new ZoneItem("crushed_iron_ore"));
		register(registry, crushed_gold_ore = new ZoneItem("crushed_gold_ore"));
		register(registry, crushed_diamond_ore = new ZoneItem("crushed_diamond_ore"));
		register(registry, crushed_emerald_ore = new ZoneItem("crushed_emerald_ore"));
		register(registry, crushed_redstone_ore = new ZoneItem("crushed_redstone_ore"));
		register(registry, crushed_coal_ore = new ZoneItem("crushed_coal_ore"));
		register(registry, crushed_lapis_ore = new ZoneItem("crushed_lapis_ore"));
		register(registry, small_pile_redstone_dust = new ZoneItem("small_pile_redstone_dust"));
		register(registry, small_pile_coal_dust = new ZoneItem("small_pile_coal_dust"));
		register(registry, small_pile_lapis_dust = new ZoneItem("small_pile_lapis_dust"));
		register(registry, tiny_pile_iron_dust = new ZoneItem("tiny_pile_iron_dust"));
		register(registry, tiny_pile_gold_dust = new ZoneItem("tiny_pile_gold_dust"));
		register(registry, tiny_pile_diamond_dust = new ZoneItem("tiny_pile_diamond_dust"));
		register(registry, tiny_pile_emerald_dust = new ZoneItem("tiny_pile_emerald_dust"));
	}

	private static void register(IForgeRegistry<Item> registry, Item item) {
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
