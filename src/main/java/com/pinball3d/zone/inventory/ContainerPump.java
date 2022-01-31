package com.pinball3d.zone.inventory;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEPump;

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

public class ContainerPump extends Container {
	private IItemHandler fluid, energy;
	private int energyTick;
	protected TEPump tileEntity;

	public ContainerPump(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEPump) tileEntity;
		fluid = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		energy = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.WEST);
		addSlotToContainer(new SlotItemHandler(fluid, 0, 80, 17) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
		});
		addSlotToContainer(new SlotItemHandler(energy, 0, 80, 53) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == ItemLoader.energy;
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
//		energyTick = tileEntity.getEnergyTick();
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, energyTick);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 0) {
			energyTick = data;
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
		} else if (newStack.getItem() == ItemLoader.energy) {
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

	public int getEnergyTick() {
		return energyTick;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
