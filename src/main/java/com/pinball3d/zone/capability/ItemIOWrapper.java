package com.pinball3d.zone.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ItemIOWrapper implements IItemHandler, IItemHandlerModifiable {
	private boolean canExtract, canInsert;
	private ItemStackHandler handler;

	public ItemIOWrapper(ItemStackHandler handler, boolean canExtract, boolean canInsert) {
		super();
		this.handler = handler;
		this.canExtract = canExtract;
		this.canInsert = canInsert;
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!canInsert) {
			return stack;
		}
		return handler.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (!canExtract) {
			return ItemStack.EMPTY;
		}
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		handler.setStackInSlot(slot, stack);
	}
}
