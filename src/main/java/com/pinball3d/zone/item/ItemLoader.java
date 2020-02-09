package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemLoader {
	public static Item hammer;

	public ItemLoader(FMLPreInitializationEvent event) {

	}

	public static void registerItems(IForgeRegistry<Item> registry) {
		TabZone.init();
		register(registry, hammer = new ItemHammer());
	}

	private static void register(IForgeRegistry<Item> registry, Item item) {
		registry.register(item);
	}
}
