package com.pinball3d.zone;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		ItemLoader.registerItems(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
//		event.getRegistry().registerAll(values);
	}
}
