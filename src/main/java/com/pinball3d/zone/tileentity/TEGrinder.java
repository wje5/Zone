package com.pinball3d.zone.tileentity;

import java.util.Arrays;

import com.pinball3d.zone.block.BlockGrinder;
import com.pinball3d.zone.recipe.Recipe;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEGrinder extends ZoneMachine {
	protected int tick, totalTick, energyTick;
	protected ItemStackHandler input, output, expectOutput;

	public TEGrinder() {
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
			Recipe recipe = RecipeHandler.getRecipe(Type.GRINDER,
					Arrays.asList(new ItemStack[] { input.getStackInSlot(0) }));
			if (recipe != null) {
				if (tryUseEnergy(true) || energyTick >= recipe.getTime()) {
					if (output.insertItem(0, recipe.getOutput(0), true).isEmpty()) {
						input.extractItem(0, recipe.getInput(0).getCount(), false);
						expectOutput.setStackInSlot(0, recipe.getOutput(0));
						tick = recipe.getTime();
						totalTick = recipe.getTime();
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
			((BlockGrinder) blockType).setState(energyTick > 0, world, pos);
		}
	}

	public int getTick() {
		return tick;
	}

	public int getTotalTick() {
		return totalTick;
	}

	public int getEnergyTick() {
		return energyTick;
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
		totalTick = compound.getInteger("totalTick");
		energyTick = compound.getInteger("energyTick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("input", input.serializeNBT());
		compound.setTag("output", output.serializeNBT());
		compound.setTag("expectOutput", expectOutput.serializeNBT());
		compound.setInteger("tick", tick);
		compound.setInteger("totalTick", totalTick);
		compound.setInteger("energyTick", energyTick);
		return compound;
	}
}
