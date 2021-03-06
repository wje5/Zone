package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockBoiler;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEBoiler extends ZoneMachine {
	protected int tick, fuelTick;
	protected ItemStackHandler fuel;

	public TEBoiler() {
		super(1);
		fuel = new ItemStackHandler();
		explosionStrength = 6.0F;
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
			tick++;
			if (tick >= 60) {
				tick -= 60;
				addEnergy(1);
				markDirty();
			}
		}
		if (fuelTick > 0 != flag) {
			BlockBoiler.setState(fuelTick > 0, world, pos);
			markDirty();
		}
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
			switch (facing) {
			case DOWN:
				return (T) energy;
			default:
				return (T) fuel;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		fuel.deserializeNBT(compound.getCompoundTag("fuel"));
		tick = compound.getInteger("tick");
		fuelTick = compound.getInteger("fuelTick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("fuel", fuel.serializeNBT());
		compound.setInteger("tick", tick);
		compound.setInteger("fuelTick", fuelTick);
		return compound;
	}
}
