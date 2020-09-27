package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockBurningBox;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEBurningBox extends TileEntity implements ITickable {
	public ItemStackHandler fuel = new ItemStackHandler();
	public int smeltTime = 0;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (smeltTime <= 0) {
				if (!fuel.getStackInSlot(0).isEmpty()) {
					BlockBurningBox.setState(true, world, pos);
					smeltTime += 200;
					fuel.getStackInSlot(0).shrink(1);
					markDirty();
				} else {
					BlockBurningBox.setState(false, world, pos);
					markDirty();
				}
			} else {
				smeltTime--;
				markDirty();
			}
		}
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
			return (T) fuel;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		fuel.deserializeNBT(tag.getCompoundTag("fuel"));
		smeltTime = tag.getInteger("smeltTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("fuel", fuel.serializeNBT());
		tag.setInteger("smeltTime", smeltTime);
		return tag;
	}
}