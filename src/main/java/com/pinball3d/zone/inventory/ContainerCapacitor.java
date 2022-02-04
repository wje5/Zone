package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TECapacitor;
import com.pinball3d.zone.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCapacitor extends Container {
	private IItemHandler battery;
	private int tier, energy, maxEnergy;
	protected TECapacitor tileEntity;
	private short energyData, maxEnergyData;

	public ContainerCapacitor(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TECapacitor) tileEntity;
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(battery, 0, 56, 17));
		addSlotToContainer(new SlotItemHandler(battery, 1, 56, 53));
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
		tier = tileEntity.getTier().getTier();
		energy = tileEntity.getEnergyStored();
		maxEnergy = tileEntity.getMaxEnergyStored();
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, tier);
			short[] s = Util.retractIntToShort(energy);
			i.sendWindowProperty(this, 1, s[0]);
			i.sendWindowProperty(this, 2, s[1]);
			s = Util.retractIntToShort(maxEnergy);
			i.sendWindowProperty(this, 3, s[0]);
			i.sendWindowProperty(this, 4, s[1]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 0) {
			tier = data;
		} else if (id == 1) {
			energyData = (short) data;
		} else if (id == 2) {
			energy = Util.combineShort(energyData, (short) data);
		} else if (id == 3) {
			maxEnergyData = (short) data;
		} else if (id == 4) {
			maxEnergy = Util.combineShort(maxEnergyData, (short) data);
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
		if (index <= 1) {
			isMerged = mergeItemStack(newStack, 2, 38, true);
		} else if ((index >= 2) && (index < 29)) {
			isMerged = mergeItemStack(newStack, 0, 2, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 29, 38, false);
			}
		} else if ((index >= 29) && (index < 38)) {
			isMerged = mergeItemStack(newStack, 0, 2, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 2, 29, false);
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

	public int getTier() {
		return tier;
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
