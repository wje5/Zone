package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.sphinx.IStorable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEStorageChest extends TENeedNetwork implements IStorable {
	private ItemStackHandler inv;

	public TEStorageChest() {
		super();
		inv = new ItemStackHandler(81);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return (T) inv;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		inv.deserializeNBT(compound.getCompoundTag("inv"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inv", inv.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public ItemStackHandler getStorage() {
		return inv;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return null;
	}
}
