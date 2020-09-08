package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEBurningBox;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBurningBox extends Container {
	private IItemHandler fuel;
	protected TEBurningBox tileEntity;
	protected int smeltTime = 0;

	public ContainerBurningBox(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEBurningBox) tileEntity;
		fuel = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		addSlotToContainer(new SlotItemHandler(fuel, 0, 80, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.COAL && stack.getItemDamage() == 1;
			}
		});
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
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		smeltTime = tileEntity.smeltTime;
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, smeltTime);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		switch (id) {
		case 0:
			this.smeltTime = data;
			break;
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
		if (index == 0) {
			isMerged = mergeItemStack(newStack, 1, 37, true);
		} else if (newStack.getItem() == Items.COAL && newStack.getItemDamage() == 1) {
			isMerged = mergeItemStack(newStack, 0, 1, false);
		} else if ((index >= 1) && (index < 28)) {
			isMerged = mergeItemStack(newStack, 28, 37, false);
		} else if ((index >= 28) && (index < 37)) {
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
