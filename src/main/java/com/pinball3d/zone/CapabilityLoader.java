package com.pinball3d.zone;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CapabilityLoader {
	@CapabilityInject(IZonePlayerCapability.class)
	public static Capability<IZonePlayerCapability> PLAYER_CAPABILITY;

	public CapabilityLoader(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(IZonePlayerCapability.class, new CapabilityZonePlayerInfo.Storage(),
				CapabilityZonePlayerInfo.Implementation::new);
	}

	@SubscribeEvent
	public static void addCap(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			ICapabilitySerializable<NBTTagCompound> provider = new CapabilityZonePlayerInfo.ProviderPlayer();
			event.addCapability(new ResourceLocation("zone:player_info"), provider);
		}
	}
}
