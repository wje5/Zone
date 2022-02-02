package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockTieredMachineLightable;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEBoiler extends ZoneTieredMachine {
	protected int fuelTick;
	protected ItemStackHandler fuel = new ItemStackHandler() {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.getItem() != ItemLoader.hybrid_fuel) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};
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

	public TEBoiler() {
		super();
	}

	public TEBoiler(Tier tier) {
		super(tier, 48000);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		boolean flag = fuelTick > 0;
		fuelTick = flag ? fuelTick - 1 : 0;
		if (fuelTick <= 0) {
			if (fuel.getStackInSlot(0).getItem() == ItemLoader.hybrid_fuel
					&& !fuel.extractItem(0, 1, false).isEmpty()) {
				fuelTick += 1200;
				if (!ConfigLoader.disableMachineSound) {
					NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 6),
							new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F,
									pos.getZ() + 0.5F, 16));
				}
				markDirty();
			}
		} else if (fuelTick % 100 == 0) {
			if (!ConfigLoader.disableMachineSound) {
				NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 6), new TargetPoint(
						world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 16));
			}
		}
		if (fuelTick > 0) {
			energy.receiveEnergy(60 * getTier().getMultiple(), false);
		}
		if (fuelTick > 0 != flag) {
			BlockTieredMachineLightable.setState(fuelTick > 0, world, pos);
			markDirty();
		}
		ItemStack stack = battery.getStackInSlot(0);
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage s = stack.getCapability(CapabilityEnergy.ENERGY, null);
			int amount = energy.extractEnergy(energy.getEnergyStored(), true);
			amount = s.receiveEnergy(amount, false);
			energy.extractEnergy(amount, false);
		}
	}

	@Override
	public boolean activeOutput(EnumFacing facing) {
		return true;
	}

	public int getFuelTick() {
		return fuelTick;
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
			if (facing == null) {
				return (T) battery;
			}
			return (T) fuel;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		fuel.deserializeNBT(compound.getCompoundTag("fuel"));
		battery.deserializeNBT(compound.getCompoundTag("battery"));
		fuelTick = compound.getInteger("fuelTick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("fuel", fuel.serializeNBT());
		compound.setTag("battery", battery.serializeNBT());
		compound.setInteger("fuelTick", fuelTick);
		return compound;
	}
}
