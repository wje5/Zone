package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEPump;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPump extends ContainerTieredMachine {
	private IItemHandler battery, fluid;

	public ContainerPump(EntityPlayer player, TEPump tileEntity) {
		super(player, tileEntity);
		fluid = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(battery, 0, 34, 44));
		addSlotToContainer(new SlotItemHandler(fluid, 0, 71, 26));
		addSlotToContainer(new SlotItemHandler(fluid, 1, 89, 26));
		addSlotToContainer(new SlotItemHandler(fluid, 2, 71, 44));
		addSlotToContainer(new SlotItemHandler(fluid, 3, 89, 44));
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
			isMerged = mergeItemStack(newStack, 36, 41, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 27, 36, false);
			}
		} else if ((index >= 27) && (index < 36)) {
			isMerged = mergeItemStack(newStack, 36, 41, false);
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
