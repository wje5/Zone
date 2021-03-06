package com.pinball3d.zone.item;

import com.pinball3d.zone.psp.ItemFC;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.IFluidBlock;
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
			transistor, energy_group, restrain_circle, theile_tube, laser_generater, crystallization_accelerator,
			redstone_crystal, dioptric_crystal, induction_tube, smoothing_unit, blaze_crystal, display_screen,
			amplify_unit, network_adapter, interference_crystal, half_interference_unit, sagger, fire_brick, clay_mold,
			ceramic_mold, redstone_crystal_lens, tar, paddle, impeller;

	public static Item energy, construct_block, terminal, fc, drill, drill_heavy, drill_head, drill_heavy_head,
			drill_empty, crucible_spoon, crucible_spoon_filled, manual, crucible, casting_table, hybrid_fuel;

	public static Item crushed_iron_ore, crushed_gold_ore, crushed_diamond_ore, crushed_emerald_ore,
			crushed_redstone_ore, crushed_coal_ore, crushed_lapis_ore, crushed_quartz_ore;

	public static Item iron_dust, gold_dust, diamond_dust, emerald_dust, coal_dust, lapis_dust, quartz_dust, stone_dust,
			netherrack_dust, etherium_dust, clarity_glass_dust, clay_dust, flint_dust, prismarine_dust,
			garton_metal_dust;

	public static Item small_pile_iron_dust, small_pile_gold_dust, small_pile_diamond_dust, small_pile_emerald_dust,
			small_pile_redstone_dust, small_pile_coal_dust, small_pile_lapis_dust, small_pile_quartz_dust,
			small_pile_stone_dust, small_pile_netherrack_dust, small_pile_etherium_dust, small_pile_clarity_glass_dust,
			small_pile_clay_dust, small_pile_flint_dust, small_pile_prismarine_dust, small_pile_garton_metal_dust,
			small_pile_glowstone_dust, small_pile_blaze_powder;

	public static Item tiny_pile_iron_dust, tiny_pile_gold_dust, tiny_pile_diamond_dust, tiny_pile_emerald_dust,
			tiny_pile_redstone_dust, tiny_pile_coal_dust, tiny_pile_lapis_dust, tiny_pile_quartz_dust,
			tiny_pile_stone_dust, tiny_pile_netherrack_dust, tiny_pile_etherium_dust, tiny_pile_clarity_glass_dust,
			tiny_pile_clay_dust, tiny_pile_flint_dust, tiny_pile_prismarine_dust, tiny_pile_garton_metal_dust,
			tiny_pile_glowstone_dust, tiny_pile_blaze_powder;

	public static Item construct_block_axis_x, construct_block_axis_y, construct_block_axis_z, truss_x, truss_z,
			processing_center_light, water, lava;

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
		register(registry, crystallization_accelerator = new ZoneItem("crystallization_accelerator"));
		register(registry, redstone_crystal = new ZoneItem("redstone_crystal"));
		register(registry, dioptric_crystal = new ZoneItem("dioptric_crystal"));
		register(registry, induction_tube = new ZoneItem("induction_tube"));
		register(registry, smoothing_unit = new ZoneItem("smoothing_unit"));
		register(registry, blaze_crystal = new ZoneItem("blaze_crystal"));
		register(registry, display_screen = new ZoneItem("display_screen"));
		register(registry, amplify_unit = new ZoneItem("amplify_unit"));
		register(registry, network_adapter = new ZoneItem("network_adapter"));
		register(registry, interference_crystal = new ZoneItem("interference_crystal"));
		register(registry, half_interference_unit = new ZoneItem("half_interference_unit"));
		register(registry, sagger = new ZoneItem("sagger"));
		register(registry, fire_brick = new ZoneItem("fire_brick"));
		register(registry, clay_mold = new ZoneItem("clay_mold"));
		register(registry, ceramic_mold = new ZoneItem("ceramic_mold"));
		register(registry, redstone_crystal_lens = new ZoneItem("redstone_crystal_lens"));
		register(registry, tar = new ZoneItem("tar"));
		register(registry, paddle = new ZoneItem("paddle"));
		register(registry, impeller = new ZoneItem("impeller"));
		register(registry, energy = new ZoneItem("energy"));
		register(registry, terminal = new ItemTerminal(), false);

		register(registry, fc = new ItemFC());
		register(registry, drill = new ItemDrill());
		register(registry, drill_heavy = new ItemDrillHeavy());
		register(registry, drill_head = new ItemDrillHead());
		register(registry, drill_heavy_head = new ItemDrillHeavyHead());
		register(registry, drill_empty = new ItemDrillEmpty());
		register(registry, crucible_spoon = new ItemCrucibleSpoon(false));
		register(registry, crucible_spoon_filled = new ItemCrucibleSpoon(true));
		register(registry, manual = new ItemManual());
		register(registry, hybrid_fuel = new ZoneItem("hybrid_fuel"));
		register(registry, crushed_iron_ore = new ZoneItem("crushed_iron_ore"));
		register(registry, crushed_gold_ore = new ZoneItem("crushed_gold_ore"));
		register(registry, crushed_diamond_ore = new ZoneItem("crushed_diamond_ore"));
		register(registry, crushed_emerald_ore = new ZoneItem("crushed_emerald_ore"));
		register(registry, crushed_redstone_ore = new ZoneItem("crushed_redstone_ore"));
		register(registry, crushed_coal_ore = new ZoneItem("crushed_coal_ore"));
		register(registry, crushed_lapis_ore = new ZoneItem("crushed_lapis_ore"));
		register(registry, crushed_quartz_ore = new ZoneItem("crushed_quartz_ore"));
		register(registry, iron_dust = new ZoneItem("iron_dust"));
		register(registry, gold_dust = new ZoneItem("gold_dust"));
		register(registry, diamond_dust = new ZoneItem("diamond_dust"));
		register(registry, emerald_dust = new ZoneItem("emerald_dust"));
		register(registry, coal_dust = new ZoneItem("coal_dust"));
		register(registry, lapis_dust = new ZoneItem("lapis_dust"));
		register(registry, quartz_dust = new ZoneItem("quartz_dust"));
		register(registry, stone_dust = new ZoneItem("stone_dust"));
		register(registry, netherrack_dust = new ZoneItem("netherrack_dust"));
		register(registry, etherium_dust = new ZoneItem("etherium_dust"));
		register(registry, clarity_glass_dust = new ZoneItem("clarity_glass_dust"));
		register(registry, clay_dust = new ZoneItem("clay_dust"));
		register(registry, flint_dust = new ZoneItem("flint_dust"));
		register(registry, prismarine_dust = new ZoneItem("prismarine_dust"));
		register(registry, garton_metal_dust = new ZoneItem("garton_metal_dust"));
		register(registry, small_pile_iron_dust = new ZoneItem("small_pile_iron_dust"));
		register(registry, small_pile_gold_dust = new ZoneItem("small_pile_gold_dust"));
		register(registry, small_pile_diamond_dust = new ZoneItem("small_pile_diamond_dust"));
		register(registry, small_pile_emerald_dust = new ZoneItem("small_pile_emerald_dust"));
		register(registry, small_pile_redstone_dust = new ZoneItem("small_pile_redstone_dust"));
		register(registry, small_pile_coal_dust = new ZoneItem("small_pile_coal_dust"));
		register(registry, small_pile_lapis_dust = new ZoneItem("small_pile_lapis_dust"));
		register(registry, small_pile_quartz_dust = new ZoneItem("small_pile_quartz_dust"));
		register(registry, small_pile_stone_dust = new ZoneItem("small_pile_stone_dust"));
		register(registry, small_pile_netherrack_dust = new ZoneItem("small_pile_netherrack_dust"));
		register(registry, small_pile_etherium_dust = new ZoneItem("small_pile_etherium_dust"));
		register(registry, small_pile_clarity_glass_dust = new ZoneItem("small_pile_clarity_glass_dust"));
		register(registry, small_pile_clay_dust = new ZoneItem("small_pile_clay_dust"));
		register(registry, small_pile_flint_dust = new ZoneItem("small_pile_flint_dust"));
		register(registry, small_pile_prismarine_dust = new ZoneItem("small_pile_prismarine_dust"));
		register(registry, small_pile_garton_metal_dust = new ZoneItem("small_pile_garton_metal_dust"));
		register(registry, small_pile_glowstone_dust = new ZoneItem("small_pile_glowstone_dust"));
		register(registry, small_pile_blaze_powder = new ZoneItem("small_pile_blaze_powder"));
		register(registry, tiny_pile_iron_dust = new ZoneItem("tiny_pile_iron_dust"));
		register(registry, tiny_pile_gold_dust = new ZoneItem("tiny_pile_gold_dust"));
		register(registry, tiny_pile_diamond_dust = new ZoneItem("tiny_pile_diamond_dust"));
		register(registry, tiny_pile_emerald_dust = new ZoneItem("tiny_pile_emerald_dust"));
		register(registry, tiny_pile_redstone_dust = new ZoneItem("tiny_pile_redstone_dust"));
		register(registry, tiny_pile_coal_dust = new ZoneItem("tiny_pile_coal_dust"));
		register(registry, tiny_pile_lapis_dust = new ZoneItem("tiny_pile_lapis_dust"));
		register(registry, tiny_pile_quartz_dust = new ZoneItem("tiny_pile_quartz_dust"));
		register(registry, tiny_pile_stone_dust = new ZoneItem("tiny_pile_stone_dust"));
		register(registry, tiny_pile_netherrack_dust = new ZoneItem("tiny_pile_netherrack_dust"));
		register(registry, tiny_pile_etherium_dust = new ZoneItem("tiny_pile_etherium_dust"));
		register(registry, tiny_pile_clarity_glass_dust = new ZoneItem("tiny_pile_clarity_glass_dust"));
		register(registry, tiny_pile_clay_dust = new ZoneItem("tiny_pile_clay_dust"));
		register(registry, tiny_pile_flint_dust = new ZoneItem("tiny_pile_flint_dust"));
		register(registry, tiny_pile_prismarine_dust = new ZoneItem("tiny_pile_prismarine_dust"));
		register(registry, tiny_pile_garton_metal_dust = new ZoneItem("tiny_pile_garton_metal_dust"));
		register(registry, tiny_pile_glowstone_dust = new ZoneItem("tiny_pile_glowstone_dust"));
		register(registry, tiny_pile_blaze_powder = new ZoneItem("tiny_pile_blaze_powder"));
		register(registry, construct_block_axis_x = new ZoneItem("construct_block_axis_x").setCreativeTab(null));
		register(registry, construct_block_axis_y = new ZoneItem("construct_block_axis_y").setCreativeTab(null));
		register(registry, construct_block_axis_z = new ZoneItem("construct_block_axis_z").setCreativeTab(null));
		register(registry, truss_x = new ZoneItem("truss_x").setCreativeTab(null));
		register(registry, truss_z = new ZoneItem("truss_z").setCreativeTab(null));
		register(registry, processing_center_light = new ZoneItem("processing_center_light").setCreativeTab(null));
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ModelLoader.setCustomModelResourceLocation(terminal, 0,
					new ModelResourceLocation("zone:terminal", "inventory"));
			ModelLoader.setCustomModelResourceLocation(terminal, 1,
					new ModelResourceLocation("zone:terminal_2", "inventory"));
		}
		Block.REGISTRY.forEach(e -> {
			if (e instanceof IFluidBlock) {
				Item item = new ItemFluid((IFluidBlock) e);
				ResourceLocation name = item.getRegistryName();
				if (!registry.containsKey(name)) {
					register(registry, item, false);
					if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
						registerRender(item, new ModelResourceLocation(e.getRegistryName(), null));
					}
				}
			}
		});
		register(registry, water = new ItemVanillaFluid(Blocks.FLOWING_WATER, "water"));
		register(registry, lava = new ItemVanillaFluid(Blocks.FLOWING_LAVA, "lava") {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 20000;
			}
		});
	}

	private static void register(IForgeRegistry<Item> registry, Item item) {
		register(registry, item, true);
	}

	private static void register(IForgeRegistry<Item> registry, Item item, boolean flag) {
		registry.register(item);
		if (flag && FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerRender(item);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item) {
		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, model);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item, ModelResourceLocation model) {
		ModelLoader.setCustomModelResourceLocation(item, 0, model);
	}
}
