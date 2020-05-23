package com.pinball3d.zone.inventory;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TETransmissionModule;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerTransmissionModule extends Container {
	private IItemHandler energy;
	protected TETransmissionModule tileEntity;

	public ContainerTransmissionModule(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TETransmissionModule) tileEntity;
		energy = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				addSlotToContainer(new SlotItemHandler(energy, j + i * 3, 62 + j * 18, 17 + i * 18) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return stack.getItem() == ItemLoader.energy;
					}
				});
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
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
		if (index <= 0) {
			isMerged = mergeItemStack(newStack, 1, 37, true);
		} else if (index >= 1 && index < 28) {
			isMerged = mergeItemStack(newStack, 28, 37, false);
		} else if (index >= 28) {
			isMerged = mergeItemStack(newStack, 1, 28, false);
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
