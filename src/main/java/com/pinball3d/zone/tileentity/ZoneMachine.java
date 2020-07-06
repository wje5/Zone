package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class ZoneMachine extends TileEntity implements ITickable {
	protected ItemStackHandler energy;

	public ZoneMachine(int energySlot) {
		super();
		energy = new ItemStackHandler(energySlot);
	}

	@Override
	public void update() {
		markDirty();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.deserializeNBT(compound.getCompoundTag("energy"));

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("energy", energy.serializeNBT());
		return compound;
	}

	public void addEnergy(int amount) {
		for (int i = 0; i < energy.getSlots(); i++) {
			int tryAmount = amount >= 64 ? 64 : amount;
			ItemStack stack = new ItemStack(ItemLoader.energy, tryAmount);
			stack = energy.insertItem(i, stack, false);
			amount = amount - tryAmount + stack.getCount();
			if (amount <= 0) {
				break;
			}
		}
		if (amount > 0) {
			world.setBlockToAir(pos);
			Explosion explosion = world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, true, true);
		}
	}

	public boolean tryUseEnergy(boolean simulate) {
		for (int i = 0; i < energy.getSlots(); i++) {
			if (energy.getStackInSlot(i).getItem() == ItemLoader.energy
					&& !energy.extractItem(i, 1, simulate).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean tryUseEnergy(int amount, boolean simulate) {
		if (!simulate && !tryUseEnergy(amount, true)) {
			return false;
		}
		for (int i = 0; i < energy.getSlots(); i++) {
			if (energy.getStackInSlot(i).getItem() == ItemLoader.energy) {
				ItemStack stack = energy.extractItem(i, amount, simulate);
				amount -= stack.getCount();
				if (amount <= 0) {
					return true;
				}
			}
		}
		return false;
	}
}
