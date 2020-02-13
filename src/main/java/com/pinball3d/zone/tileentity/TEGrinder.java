package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.recipe.Recipe;
import com.pinball3d.zone.recipe.RecipeHandler;
import com.pinball3d.zone.recipe.RecipeHandler.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import scala.actors.threadpool.Arrays;

public class TEGrinder extends ZoneMachine {
	protected int tick;
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
		tick = tick < 1 ? 0 : tick - 1;
		if (tick <= 0) {
			output.insertItem(0, expectOutput.getStackInSlot(0), false);
			expectOutput.setStackInSlot(0, ItemStack.EMPTY);
			Recipe recipe = RecipeHandler.getRecipe(Type.GRINDER,
					Arrays.asList(new ItemStack[] { input.getStackInSlot(0) }));
			if (recipe != null) {
				if (output.insertItem(0, recipe.getOutput(0), true).isEmpty()) {
					input.extractItem(0, recipe.getInput(0).getCount(), false);
					expectOutput.setStackInSlot(0, recipe.getOutput(0));
					tick = recipe.getTime();
				}
			}
		}
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("input", input.serializeNBT());
		compound.setTag("output", output.serializeNBT());
		compound.setTag("expectOutput", expectOutput.serializeNBT());
		compound.setInteger("tick", tick);
		return compound;
	}
}
