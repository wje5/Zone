package com.pinball3d.zone.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TECapacitor extends ZoneTieredMachine {
	protected ItemStackHandler battery = new ItemStackHandler(2) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!stack.hasCapability(CapabilityEnergy.ENERGY, null)
					|| (slot == 0 && !stack.getCapability(CapabilityEnergy.ENERGY, null).canExtract()
							|| slot == 1 && !stack.getCapability(CapabilityEnergy.ENERGY, null).canReceive())) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};

	public TECapacitor() {
		super();
	}

	public TECapacitor(Tier tier) {
		super(tier, 1600000);
	}

	@Override
	public void update() {
		super.update();
		ItemStack stack = battery.getStackInSlot(0);
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage s = stack.getCapability(CapabilityEnergy.ENERGY, null);
			int amount = s.extractEnergy(s.getEnergyStored(), true);
			amount = energy.receiveEnergy(amount, false);
			s.extractEnergy(amount, false);
		}
		stack = battery.getStackInSlot(1);
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage s = stack.getCapability(CapabilityEnergy.ENERGY, null);
			int amount = energy.extractEnergy(energy.getEnergyStored(), true);
			amount = s.receiveEnergy(amount, false);
			energy.extractEnergy(amount, false);
		}
	}

	@Override
	public boolean activeOutput(EnumFacing facing) {
		return facing == EnumFacing.UP ? true : super.activeOutput(facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == null) {
				return true;
			}
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == null) {
				return (T) battery;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		battery.deserializeNBT(compound.getCompoundTag("battery"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("battery", battery.serializeNBT());
		return compound;
	}
}
