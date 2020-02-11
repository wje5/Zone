package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class ItemLoader {
	public static Item hammer, spanner, file, saw;

	public static Item iron_plate, rivet, iron_plate_riveted;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		TabZone.init();
		register(registry, hammer = new ItemCraftTool("hammer"));
		register(registry, spanner = new ItemCraftTool("spanner"));
		register(registry, file = new ItemCraftTool("file"));
		register(registry, saw = new ItemCraftTool("saw"));
		register(registry, iron_plate = new ZoneItem("iron_plate"));
		register(registry, rivet = new ZoneItem("rivet"));
		register(registry, iron_plate_riveted = new ZoneItem("iron_plate_riveted"));
	}

	private static void register(IForgeRegistry<Item> registry, Item item) {
		registry.register(item);
		registerRender(item);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item) {
		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, model);
	}
}
