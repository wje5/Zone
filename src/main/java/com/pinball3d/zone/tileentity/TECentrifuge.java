package com.pinball3d.zone.tileentity;

import java.util.Arrays;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockCentrifuge;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.recipe.Recipe;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TECentrifuge extends ZoneMachine {
	protected int tick, totalTick, energyTick;
	protected ItemStackHandler input, output, expectOutput;

	public TECentrifuge() {
		super(1);
		input = new ItemStackHandler();
		output = new ItemStackHandler(3);
		expectOutput = new ItemStackHandler(3);
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
			output.insertItem(1, expectOutput.getStackInSlot(1), false);
			output.insertItem(2, expectOutput.getStackInSlot(2), false);
			expectOutput.setStackInSlot(0, ItemStack.EMPTY);
			expectOutput.setStackInSlot(1, ItemStack.EMPTY);
			expectOutput.setStackInSlot(2, ItemStack.EMPTY);
			Recipe recipe = RecipeHandler.getRecipe(Type.CENTRIFUGE,
					Arrays.asList(new ItemStack[] { input.getStackInSlot(0) }));
			if (recipe != null) {
				if (tryUseEnergy(true) || energyTick >= recipe.getTime()) {
					if (output.insertItem(0, recipe.getOutput(0), true).isEmpty()
							&& output.insertItem(1, recipe.getOutput(1), true).isEmpty()
							&& output.insertItem(2, recipe.getOutput(2), true).isEmpty()) {
						input.extractItem(0, recipe.getInput(0).getCount(), false);
						expectOutput.setStackInSlot(0, recipe.getOutput(0));
						expectOutput.setStackInSlot(1, recipe.getOutput(1));
						expectOutput.setStackInSlot(2, recipe.getOutput(2));
						tick = recipe.getTime();
						totalTick = recipe.getTime();
						if (!ConfigLoader.disableMachineSound) {
							NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 4),
									new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F,
											pos.getZ() + 0.5F, 16));
						}
					}
				}
			}
		}
		energyTick = energyTick < 1 ? 0 : energyTick - 1;
		if (energyTick <= 0 && tick > 0) {
			if (tryUseEnergy(false)) {
				energyTick += 100;
			} else {
				tick = 0;
				expectOutput.setStackInSlot(0, ItemStack.EMPTY);
			}
		}
		if (energyTick > 0 != flag) {
			BlockCentrifuge.setState(energyTick > 0, world, pos);
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
