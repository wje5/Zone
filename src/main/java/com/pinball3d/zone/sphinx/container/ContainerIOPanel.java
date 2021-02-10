package com.pinball3d.zone.sphinx.container;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.TEIOPanel;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIOPanel extends ContainerSphinxBase {
	public IItemHandler inv, global;
	public TEIOPanel tileEntity;
	public int x, y;
	public EntityPlayer player;

	public ContainerIOPanel(EntityPlayer player, TileEntity tileEntity) {
		super(player);
		this.player = player;
		this.tileEntity = (TEIOPanel) tileEntity;
		inv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		global = new ItemStackHandler(36);
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new SlotItemHandler(inv, j + i * 9, 100 + j * 19, 10 + i * 19));
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 100 + j * 19, 129 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 100 + i * 19, 189));
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
				isMerged = mergeItemStack(newStack, 81, 90, true);
			}
		} else {
			isMerged = mergeItemStack(newStack, 0, 54, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 54, 81, true);
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

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!super.canInteractWith(playerIn)) {
			return false;
		}
		UUID network = tileEntity.getNetwork();
		if (network == null) {
			return true;
		}
		WorldPos pos = GlobalNetworkData.getPos(network);
		if (pos.isOrigin()) {
			return true;
		}
		TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
		return te.isUser(playerIn);
	}
}
