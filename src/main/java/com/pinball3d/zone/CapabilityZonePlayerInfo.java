package com.pinball3d.zone;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CapabilityZonePlayerInfo {
	@SubscribeEvent
	public static void reSyncDataOnPlayerDeath(PlayerEvent.Clone event) {
		IZonePlayerCapability cap = event.getEntityPlayer().getCapability(CapabilityLoader.PLAYER_CAPABILITY, null);
		if (cap != null) {
			IZonePlayerCapability original = event.getOriginal().getCapability(CapabilityLoader.PLAYER_CAPABILITY,
					null);
			cap.setInited(original.isInited());
		}
	}

	public static class Implementation implements IZonePlayerCapability {
		private boolean inited;

		@Override
		public boolean isInited() {
			return inited;
		}

		@Override
		public void setInited(boolean flag) {
			inited = flag;
		}

	}

	public static class Storage implements Capability.IStorage<IZonePlayerCapability> {
		@Override
		public NBTBase writeNBT(Capability<IZonePlayerCapability> capability, IZonePlayerCapability instance,
				EnumFacing side) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("inited", instance.isInited());
			return tag;
		}

		@Override
		public void readNBT(Capability<IZonePlayerCapability> capability, IZonePlayerCapability instance,
				EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				NBTTagCompound tag = (NBTTagCompound) nbt;
				instance.setInited(tag.getBoolean("inited"));
			}
		}
	}

	public static class ProviderPlayer implements ICapabilitySerializable<NBTTagCompound> {
		private IZonePlayerCapability implementation = new Implementation();
		private IStorage<IZonePlayerCapability> storage = CapabilityLoader.PLAYER_CAPABILITY.getStorage();

		@Override
		public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
			return CapabilityLoader.PLAYER_CAPABILITY.equals(cap);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
			if (CapabilityLoader.PLAYER_CAPABILITY.equals(cap)) {
				return (T) implementation;
			} else {
				return null;
			}
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag("implementation",
					storage.writeNBT(CapabilityLoader.PLAYER_CAPABILITY, implementation, null));
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			storage.readNBT(CapabilityLoader.PLAYER_CAPABILITY, implementation, null,
					compound.getCompoundTag("implementation"));
		}
	}
}
