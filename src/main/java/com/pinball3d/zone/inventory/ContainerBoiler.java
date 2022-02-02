package com.pinball3d.zone.inventory;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEBoiler;
import com.pinball3d.zone.util.Util;

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

public class ContainerBoiler extends Container {
	private IItemHandler fuel, battery;
	private int fuelTick, energy, maxEnergy;
	protected TEBoiler tileEntity;
	private short energyData, maxEnergyData;

	public ContainerBoiler(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEBoiler) tileEntity;

		fuel = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		battery = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		addSlotToContainer(new SlotItemHandler(fuel, 0, 49, 53));
		addSlotToContainer(new SlotItemHandler(battery, 0, 111, 53));
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
		fuelTick = tileEntity.getFuelTick();
		energy = tileEntity.getEnergyStored();
		maxEnergy = tileEntity.getMaxEnergyStored();
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, fuelTick);
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
			fuelTick = data;
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
		} else if (newStack.getItem() == ItemLoader.hybrid_fuel) {
			isMerged = mergeItemStack(newStack, 1, 2, false);
		} else if ((index >= 2) && (index < 29)) {
			isMerged = mergeItemStack(newStack, 29, 38, false);
		} else if ((index >= 29) && (index < 38)) {
			isMerged = mergeItemStack(newStack, 2, 29, false);
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

	public int getFuelTick() {
		return fuelTick;
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
