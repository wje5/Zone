package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockElecFurnace;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEElecFurnace extends ZoneMachine {
	protected int tick, energyTick;
	protected ItemStackHandler input, output, expectOutput;

	public TEElecFurnace() {
		super(1);
		input = new ItemStackHandler();
		output = new ItemStackHandler();
		expectOutput = new ItemStackHandler();
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		boolean flag = energyTick > 0;
		tick = tick < 1 ? 0 : tick - 1;
		if (tick <= 0) {
			output.insertItem(0, expectOutput.getStackInSlot(0), false);
			expectOutput.setStackInSlot(0, ItemStack.EMPTY);
			if (tryUseEnergy(true) || energyTick >= 100) {
				ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(input.getStackInSlot(0)).copy();
				if (!stack.isEmpty()) {
					if (output.insertItem(0, stack, true).isEmpty()) {
						input.extractItem(0, 1, false);
						expectOutput.setStackInSlot(0, stack);
						tick = 100;
						NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 3),
								new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F,
										pos.getZ() + 0.5F, 16));
					}
				}
			}
		}
		energyTick = energyTick < 1 ? 0 : energyTick - 1;
		if (energyTick <= 0 && tick > 0) {
			if (tryUseEnergy(false)) {
				energyTick += 400;
			} else {
				tick = 0;
				expectOutput.setStackInSlot(0, ItemStack.EMPTY);
			}
		}
		if (energyTick > 0 != flag) {
			((BlockElecFurnace) blockType).setState(energyTick > 0, world, pos);
		}
	}

	public int getTick() {
		return tick;
	}

	public int getEnergyTick() {
		return energyTick;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			switch (facing) {
			case UP:
				return (T) input;
			case DOWN:
				return (T) output;
			default:
				return (T) energy;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		input.deserializeNBT(compound.getCompoundTag("input"));
		output.deserializeNBT(compound.getCompoundTag("output"));
		expectOutput.deserializeNBT(compound.getCompoundTag("expectOutput"));
		tick = compound.getInteger("tick");
		energyTick = compound.getInteger("energyTick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("input", input.serializeNBT());
		compound.setTag("output", output.serializeNBT());
		compound.setTag("expectOutput", expectOutput.serializeNBT());
		compound.setInteger("tick", tick);
		compound.setInteger("energyTick", energyTick);
		return compound;
	}
}
