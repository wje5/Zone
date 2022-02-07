package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockTieredMachineLightable;
import com.pinball3d.zone.capability.ItemIOWrapper;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEElecFurnace extends ZoneTieredMachine {
	protected int tick;
	protected ItemStackHandler input = new ItemStackHandler();
	protected ItemStackHandler output = new ItemStackHandler();
	protected ItemStackHandler expectOutput = new ItemStackHandler();
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

	public TEElecFurnace() {
		super();
	}

	public TEElecFurnace(Tier tier) {
		super(tier, 8000);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		ItemStack stack = battery.getStackInSlot(0);
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
			IEnergyStorage s = stack.getCapability(CapabilityEnergy.ENERGY, null);
			int amount = s.extractEnergy(s.getEnergyStored(), true);
			amount = energy.receiveEnergy(amount, false);
			s.extractEnergy(amount, false);
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
				stack = FurnaceRecipes.instance().getSmeltingResult(input.getStackInSlot(0)).copy();
				if (!stack.isEmpty() && energy.extractEnergy(2000, true) == 2000) {
					if (output.insertItem(0, stack, true).isEmpty()) {
						input.extractItem(0, 1, false);
						expectOutput.setStackInSlot(0, stack);
						tick = 100;
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
		return 100;
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
			if (facing == EnumFacing.DOWN) {
				return (T) new ItemIOWrapper(output, true, false);
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
		battery.deserializeNBT(compound.getCompoundTag("battery"));
		tick = compound.getInteger("tick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("input", input.serializeNBT());
		compound.setTag("output", output.serializeNBT());
		compound.setTag("expectOutput", expectOutput.serializeNBT());
		compound.setTag("battery", battery.serializeNBT());
		compound.setInteger("tick", tick);
		return compound;
	}
}
