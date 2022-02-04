package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEAlloySmelter;

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

public class ContainerAlloySmelter extends ContainerTieredMachine {
	private IItemHandler battery, input, output;
	private int tick, totalTick;

	public ContainerAlloySmelter(EntityPlayer player, TEAlloySmelter tileEntity) {
		super(player, tileEntity);
		this.tileEntity = tileEntity;
		input = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		output = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(input, 0, 38, 17));
		addSlotToContainer(new SlotItemHandler(input, 1, 56, 17));
		addSlotToContainer(new SlotItemHandler(input, 2, 74, 17));
		addSlotToContainer(new SlotItemHandler(output, 0, 116, 35));
		addSlotToContainer(new SlotItemHandler(battery, 0, 56, 53));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		tick = ((TEAlloySmelter) tileEntity).getTick();
		totalTick = ((TEAlloySmelter) tileEntity).getTotalTick();
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
		if (index <= 4) {
			isMerged = mergeItemStack(newStack, 5, 41, true);
		} else if (index >= 5 && index < 32) {
			isMerged = mergeItemStack(newStack, 0, 4, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 32, 41, false);
			}
		} else if (index >= 32) {
			isMerged = mergeItemStack(newStack, 0, 4, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 5, 32, false);
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
