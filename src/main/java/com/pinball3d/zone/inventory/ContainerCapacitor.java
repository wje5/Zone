package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TECapacitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCapacitor extends ContainerTieredMachine {
	private IItemHandler battery;

	public ContainerCapacitor(EntityPlayer player, TECapacitor tileEntity) {
		super(player, tileEntity);
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(battery, 0, 56, 17));
		addSlotToContainer(new SlotItemHandler(battery, 1, 56, 53));
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
			isMerged = mergeItemStack(newStack, 36, 38, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 27, 36, false);
			}
		} else if ((index >= 27) && (index < 36)) {
			isMerged = mergeItemStack(newStack, 36, 38, false);
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
