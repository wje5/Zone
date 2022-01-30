package com.pinball3d.zone.tileentity;

import java.util.Arrays;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockTieredMachineLightable;
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

public class TEFormingPress extends ZoneTieredMachine {
	protected int tick, totalTick;
	protected ItemStackHandler input = new ItemStackHandler(2);
	protected ItemStackHandler output = new ItemStackHandler();
	protected ItemStackHandler expectOutput = new ItemStackHandler();

	public TEFormingPress() {
		super();
	}

	public TEFormingPress(Tier tier) {
		super(tier, 8000);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		boolean flag = tick > 0;
		int work = getTier().getMultiple();
		while (work > 0) {
			if (tick > 0) {
				int m = Math.min(work, tick);
				if (energy.extractEnergy(m * 20, false) < m * 20) {
					tick = 0;
					expectOutput.setStackInSlot(0, ItemStack.EMPTY);
					break;
				} else {
					tick -= m;
					work -= m;
				}
			}
			if (tick <= 0) {
				output.insertItem(0, expectOutput.getStackInSlot(0), false);
				expectOutput.setStackInSlot(0, ItemStack.EMPTY);
				Recipe recipe = RecipeHandler.getRecipe(Type.FORMING_PRESS,
						Arrays.asList(new ItemStack[] { input.getStackInSlot(0), input.getStackInSlot(1) }));
				if (recipe != null && energy.extractEnergy(20 * recipe.getTime(), true) == 20 * recipe.getTime()) {
					if (output.insertItem(0, recipe.getOutput(0), true).isEmpty()) {
						input.extractItem(0, recipe.getInput(0).getCount(), false);
						input.extractItem(1, recipe.getInput(1).getCount(), false);
						expectOutput.setStackInSlot(0, recipe.getOutput(0));
						tick = recipe.getTime();
						totalTick = tick;
						if (!ConfigLoader.disableMachineSound) {
							NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 2),
									new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F,
											pos.getZ() + 0.5F, 16));
						}
					} else {
						break;
					}
				} else {
					break;
				}
			}
		}
		if (tick > 0 != flag) {
			BlockTieredMachineLightable.setState(tick > 0, world, pos);
		}
	}

	public int getTick() {
		return tick;
	}

	public int getTotalTick() {
		return totalTick;
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
			if (facing == EnumFacing.DOWN) {
				return (T) output;
			}
			return (T) input;
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("input", input.serializeNBT());
		compound.setTag("output", output.serializeNBT());
		compound.setTag("expectOutput", expectOutput.serializeNBT());
		compound.setInteger("tick", tick);
		compound.setInteger("totalTick", totalTick);
		return compound;
	}
}
