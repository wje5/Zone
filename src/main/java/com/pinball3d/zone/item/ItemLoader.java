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
			etherium_plate_riveted, kinetic_amplifier, etherium_rod, processing_unit, transistor;

	public static Item energy;

	public static Item crushed_iron_ore, crushed_gold_ore;

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
		register(registry, processing_unit = new ZoneItem("processing_unit"));
		register(registry, transistor = new ZoneItem("transistor"));
		register(registry, energy = new ZoneItem("energy"));
		register(registry, crushed_iron_ore = new ZoneItem("crushed_iron_ore"));
		register(registry, crushed_gold_ore = new ZoneItem("crushed_gold_ore"));
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
