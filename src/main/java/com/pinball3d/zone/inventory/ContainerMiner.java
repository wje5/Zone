package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEMiner;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMiner extends ContainerTieredMachine {
	private IItemHandler battery, storage;
	protected TEMiner tileEntity;

	public ContainerMiner(EntityPlayer player, TEMiner tileEntity) {
		super(player, tileEntity);
		storage = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(battery, 0, 15, 53));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 5; ++j) {
				addSlotToContainer(new SlotItemHandler(storage, j + i * 5, 44 + j * 18, 17 + i * 18));
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);
		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}
		ItemStack newStack = slot.getStack(), oldStack = newStack.copy();
		boolean isMerged = false;
		if (index >= 36) {
			isMerged = mergeItemStack(newStack, 0, 36, true);
		} else if (index < 27) {
			isMerged = mergeItemStack(newStack, 36, 52, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 27, 36, false);
			}
		} else if ((index >= 27) && (index < 36)) {
			isMerged = mergeItemStack(newStack, 36, 52, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 0, 27, false);
			}
		}
		if (!isMerged) {
			return ItemStack.EMPTY;
		}
		if (newStack.getCount() == 0) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		slot.onTake(playerIn, newStack);
		return oldStack;
	}
}
