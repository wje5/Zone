package com.pinball3d.zone.inventory;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEAlloySmelter;

import net.minecraft.entity.player.EntityPlayer;
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

public class ContainerAlloySmelter extends Container {
	private IItemHandler energy, input, output;
	private int tick, totalTick, energyTick;
	protected TEAlloySmelter tileEntity;

	public ContainerAlloySmelter(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEAlloySmelter) tileEntity;
		energy = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.WEST);
		input = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		output = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		addSlotToContainer(new SlotItemHandler(energy, 0, 56, 53) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == ItemLoader.energy;
			}
		});
		addSlotToContainer(new SlotItemHandler(input, 0, 38, 17));
		addSlotToContainer(new SlotItemHandler(input, 1, 56, 17));
		addSlotToContainer(new SlotItemHandler(input, 2, 74, 17));
		addSlotToContainer(new SlotItemHandler(output, 0, 116, 35));
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
		tick = tileEntity.getTick();
		totalTick = tileEntity.getTotalTick();
		energyTick = tileEntity.getEnergyTick();
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, tick);
			i.sendWindowProperty(this, 1, totalTick);
			i.sendWindowProperty(this, 2, energyTick);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		switch (id) {
		case 0:
			tick = data;
			break;
		case 1:
			totalTick = data;
			break;
		case 2:
			energyTick = data;
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

	public int getEnergyTick() {
		return energyTick;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
