package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockDynavoltRestrainer;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.block.BlockProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEDynavoltRestrainer extends TileEntity implements ITickable {
	protected ItemStackHandler battery = new ItemStackHandler() {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!stack.hasCapability(CapabilityEnergy.ENERGY, null)
					|| !stack.getCapability(CapabilityEnergy.ENERGY, null).canExtract()) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};

	public TEDynavoltRestrainer() {

	}

	@Override
	public void update() {
		ItemStack stack = battery.getStackInSlot(0);
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage s = stack.getCapability(CapabilityEnergy.ENERGY, null);
			WorldPos center = getProcessingCenterPos(new WorldPos(pos, world));
			if (!center.isOrigin()) {
				TEProcessingCenter te = (TEProcessingCenter) center.getTileEntity();
				int amount = s.extractEnergy(s.getEnergyStored(), true);
				amount = te.getEnergy().receiveEnergy(amount, false);
				s.extractEnergy(amount, false);
			}
		}
	}

	public static WorldPos getProcessingCenterPos(WorldPos pos) {
		IBlockState state = pos.getBlockState();
		if (state != null && state.getBlock() == BlockLoader.dynavolt_restrainer) {
			pos = new WorldPos(
					pos.getPos().offset(state.getValue(BlockDynavoltRestrainer.FACING).getOpposite(), 3).add(0, 2, 0),
					pos.getDim());
			if (pos.getBlockState().getBlock() instanceof BlockProcessingCenter) {
				return pos;
			}
		}
		return WorldPos.ORIGIN;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == null) {
			return true;
		}
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			WorldPos center = getProcessingCenterPos(new WorldPos(pos, world));
			if (!center.isOrigin()) {
				return true;
			}
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == null) {
			return (T) battery;
		}
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			WorldPos center = getProcessingCenterPos(new WorldPos(pos, world));
			if (!center.isOrigin()) {
				TEProcessingCenter te = (TEProcessingCenter) center.getTileEntity();
				return (T) te.getEnergy();
			}
		}
		return super.getCapability(capability, facing);
	}

	public int getEnergyStored() {
		WorldPos center = getProcessingCenterPos(new WorldPos(pos, world));
		if (!center.isOrigin()) {
			TEProcessingCenter te = (TEProcessingCenter) center.getTileEntity();
			return te.getEnergy().getEnergyStored();
		}
		return 0;
	}

	public int getMaxEnergyStored() {
		WorldPos center = getProcessingCenterPos(new WorldPos(pos, world));
		if (!center.isOrigin()) {
			TEProcessingCenter te = (TEProcessingCenter) center.getTileEntity();
			return te.getEnergy().getMaxEnergyStored();
		}
		return 0;
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
