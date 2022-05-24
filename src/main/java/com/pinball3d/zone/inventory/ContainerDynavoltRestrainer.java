package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEDynavoltRestrainer;
import com.pinball3d.zone.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDynavoltRestrainer extends Container {
	protected TEDynavoltRestrainer tileEntity;
	private IItemHandler battery;
	private int energy, maxEnergy;
	private short energyData, maxEnergyData;

	public ContainerDynavoltRestrainer(EntityPlayer player, TEDynavoltRestrainer tileEntity) {
		this.tileEntity = tileEntity;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(battery, 0, 56, 35));
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
			isMerged = mergeItemStack(newStack, 36, 37, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 27, 36, false);
			}
		} else if ((index >= 27) && (index < 36)) {
			isMerged = mergeItemStack(newStack, 36, 37, false);
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

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (!tileEntity.getWorld().isRemote) {
			energy = tileEntity.getEnergyStored();
			maxEnergy = tileEntity.getMaxEnergyStored();
		}
		for (IContainerListener i : listeners) {
			short[] s = Util.retractIntToShort(energy);
			i.sendWindowProperty(this, 0, s[0]);
			i.sendWindowProperty(this, 1, s[1]);
			s = Util.retractIntToShort(maxEnergy);
			i.sendWindowProperty(this, 2, s[0]);
			i.sendWindowProperty(this, 3, s[1]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 0) {
			energyData = (short) data;
		} else if (id == 1) {
			energy = Util.combineShort(energyData, (short) data);
		} else if (id == 2) {
			maxEnergyData = (short) data;
		} else if (id == 3) {
			maxEnergy = Util.combineShort(maxEnergyData, (short) data);
		}
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
