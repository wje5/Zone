package com.pinball3d.zone.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod.EventBusSubscriber
public class EntityLoader {
	private static int nextID = 0;

	@SubscribeEvent
	public static void onEntityRegistation(RegistryEvent.Register<EntityEntry> event) {
		event.getRegistry().register(EntityEntryBuilder.create().entity(EntityBullet.class)
				.id(new ResourceLocation("zone", "bullet"), 233).name("Bullet").tracker(64, 1, false).build());
	}
}
