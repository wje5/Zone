package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEIOPanel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIOPanel extends Container {
	private IItemHandler inv, global;
	protected TEIOPanel tileEntity;

	public ContainerIOPanel(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEIOPanel) tileEntity;
		inv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		global = this.tileEntity.getGlobalInv();
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new SlotItemHandler(inv, j + i * 9, 54 + j * 19, 10 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			for (int j = 0; j < 4; ++j) {
				addSlotToContainer(new SlotItemHandler(global, j + i * 4, j * 19 - 38, 29 + i * 19) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						return false;
					}
				});
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 54 + j * 19, 129 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 54 + i * 19, 189));
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
		if (index <= 53) {
			isMerged = mergeItemStack(newStack, 54, 90, true);
		} else if (index >= 54 && index < 81) {
			isMerged = mergeItemStack(newStack, 0, 54, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 81, 90, false);
			}
		} else if (index >= 81) {
			isMerged = mergeItemStack(newStack, 0, 81, false);
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

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
