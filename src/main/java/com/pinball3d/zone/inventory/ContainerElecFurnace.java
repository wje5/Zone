package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEElecFurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerElecFurnace extends ContainerTieredMachine {
	private IItemHandler battery, input, output;
	private int tick, totalTick;

	public ContainerElecFurnace(EntityPlayer player, TEElecFurnace tileEntity) {
		super(player, tileEntity);
		input = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		output = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(battery, 0, 56, 53));
		addSlotToContainer(new SlotItemHandler(input, 0, 56, 17));
		addSlotToContainer(new SlotItemHandler(output, 0, 116, 35));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (!tileEntity.getWorld().isRemote) {
			tick = ((TEElecFurnace) tileEntity).getTick();
			totalTick = ((TEElecFurnace) tileEntity).getTotalTick();
		}
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 5, tick);
			i.sendWindowProperty(this, 6, totalTick);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		switch (id) {
		case 5:
			tick = data;
			break;
		case 6:
			totalTick = data;
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
		if (index >= 36) {
			isMerged = mergeItemStack(newStack, 0, 36, true);
		} else if (index < 27) {
			isMerged = mergeItemStack(newStack, 36, 39, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 27, 36, false);
			}
		} else if ((index >= 27) && (index < 36)) {
			isMerged = mergeItemStack(newStack, 36, 39, false);
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

	public int getTick() {
		return tick;
	}

	public int getTotalTick() {
		return totalTick;
	}
}
