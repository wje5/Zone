package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.SoundUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TEDrainer extends ZoneMachine {
	protected int tick;

	public TEDrainer() {
		super(1);
	}

	@Override
	public void update() {
		super.update();
		tick++;
		if (world.isRemote) {
			if (tick % 200 == 0) {
				world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
						SoundUtil.getSoundEventFromId(1), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
			return;
		}
		if (tick >= 1200) {
			tick -= 1200;
			addEnergy(1);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == EnumFacing.DOWN) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && facing == EnumFacing.DOWN) {
			return (T) energy;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tick = compound.getInteger("tick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("tick", tick);
		return compound;
	}
}
