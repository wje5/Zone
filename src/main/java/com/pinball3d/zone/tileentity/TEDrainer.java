package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.SoundUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEDrainer extends ZoneTieredMachine {
	protected int tick;
	protected ItemStackHandler battery = new ItemStackHandler() {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!stack.hasCapability(CapabilityEnergy.ENERGY, null)
					|| !stack.getCapability(CapabilityEnergy.ENERGY, null).canReceive()) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};

	public TEDrainer() {
		super();
	}

	public TEDrainer(Tier tier) {
		super(tier, 8000);
	}

	@Override
	public void update() {
		super.update();
		tick++;
		if (world.isRemote) {
			if (tick % 200 == 0 && !ConfigLoader.disableMachineSound) {
				world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
						SoundUtil.getSoundEventFromId(1), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
			return;
		}
		energy.receiveEnergy(10 * getTier().getMultiple(), false);
		ItemStack stack = battery.getStackInSlot(0);
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage s = stack.getCapability(CapabilityEnergy.ENERGY, null);
			int amount = energy.extractEnergy(energy.getEnergyStored(), true);
			amount = s.receiveEnergy(amount, false);
			energy.extractEnergy(amount, false);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == null) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == null) {
			return (T) battery;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean activeOutput(EnumFacing facing) {
		return true;
	}

	@Override
	public boolean canReceiveEnergy(EnumFacing facing) {
		return false;
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
