package com.pinball3d.zone.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class EntityLoader {
	private static int id = 0;

	@SubscribeEvent
	public static void onEntityRegistation(RegistryEvent.Register<EntityEntry> event) {
		register(event.getRegistry(), EntityPiano.class, "piano", "Piano");
	}

	public static void register(IForgeRegistry<EntityEntry> registry, Class<? extends Entity> clazz, String entityId,
			String name) {
		registry.register(EntityEntryBuilder.create().entity(clazz).id(new ResourceLocation("zone", entityId), id++)
				.name(name).tracker(80, 3, false).build());
	}
}
