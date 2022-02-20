package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TECableGeneral;
import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig;
import com.pinball3d.zone.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

public class ContainerCable2 extends Container {
	private int tier, energy, maxEnergy;
	private short energyData, maxEnergyData;
	protected TECableGeneral tileEntity;
	public final EnumFacing facing;

	public ContainerCable2(EntityPlayer player, TECableGeneral tileEntity, EnumFacing facing) {
		this.tileEntity = tileEntity;
		this.facing = facing;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 5; ++j) {
				addSlotToContainer(new TagSlot(tileEntity.getConfig(facing), j + i * 5, 80 + j * 18, 17 + i * 18));
			}
		}
		// 一个问题 叠加的物品右键拖过多个物品格的时候会试图在每个空格子里放进一个
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		Slot slot = (slotId < 0) ? null : inventorySlots.get(slotId);
		if (slot instanceof TagSlot) {
			if (dragType == 2) {
				slot.putStack(ItemStack.EMPTY);
				slot.onSlotChanged();
			} else {
				slot.putStack(player.inventory.getItemStack().isEmpty() ? ItemStack.EMPTY
						: player.inventory.getItemStack().copy());
			}
			return player.inventory.getItemStack();
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotMin, int slotMax, boolean ascending) {
		boolean slotFound = false;
		int k = ascending ? (slotMax - 1) : slotMin;
		if (stack.isStackable()) {
			while (stack.getCount() > 0 && ((!ascending && k < slotMax) || (ascending && k >= slotMin))) {
				Slot slot = this.inventorySlots.get(k);
				ItemStack stackInSlot = slot.getStack();
				if (slot.isItemValid(stack) && ItemStack.areItemsEqualIgnoreDurability(stack, stackInSlot)
						&& ItemStack.areItemStackShareTagsEqual(stack, stackInSlot)) {
					int l = stackInSlot.getCount() + stack.getCount();
					int slotLimit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					if (l <= slotLimit) {
						stack.setCount(0);
						stackInSlot.setCount(l);
						slot.onSlotChanged();
						slotFound = true;
					} else if (stackInSlot.getCount() < slotLimit) {
						stack.setCount(slotLimit - stackInSlot.getCount());
						stackInSlot.setCount(slotLimit);
						slot.onSlotChanged();
						slotFound = true;
					}
				}
				k += ascending ? -1 : 1;
			}
		}
		if (stack.getCount() > 0) {
			k = ascending ? (slotMax - 1) : slotMin;
			while ((!ascending && k < slotMax) || (ascending && k >= slotMin)) {
				Slot slot = this.inventorySlots.get(k);
				ItemStack stackInSlot = slot.getStack();
				if (slot.isItemValid(stack) && stackInSlot.isEmpty()) {
					slot.putStack(ItemHandlerHelper.copyStackWithSize(stack,
							Math.min(stack.getCount(), slot.getSlotStackLimit())));
					slot.onSlotChanged();
					if (!slot.getStack().isEmpty()) {
						stack.setCount(slot.getStack().getCount());
						slotFound = true;
					}
					break;
				}
				k += ascending ? -1 : 1;
			}
		}
		return slotFound;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = this.inventorySlots.get(index);
		int invPlayer = 27;
		int invFull = invPlayer + 9;
		int invTile = invFull + 15;
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			if (index < 0) {
				return ItemStack.EMPTY;
			} else if (index < invFull) {
				Slot k = null;
				for (int i = invFull; i < invTile; i++) {
					Slot slot1 = this.inventorySlots.get(i);
					if (!slot1.getHasStack()) {
						if (k == null) {
							k = slot1;
						}
					} else if (ItemStack.areItemsEqualIgnoreDurability(stack, slot1.getStack())
							&& ItemStack.areItemStackShareTagsEqual(stack, slot1.getStack())) {
						return ItemStack.EMPTY;
					}
				}
				if (k != null) {
					k.putStack(stack.copy());
				}
				return ItemStack.EMPTY;
			}
			slot.putStack(ItemStack.EMPTY);
			slot.onSlotChanged();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
//		if (!tileEntity.getWorld().isRemote) {
//			tier = tileEntity.getTier().getTier();
//			energy = tileEntity.getEnergyStored();
//			maxEnergy = tileEntity.getMaxEnergyStored();
//		}
//		for (IContainerListener i : listeners) {
//			i.sendWindowProperty(this, 0, tier);
//			short[] s = Util.retractIntToShort(energy);
//			i.sendWindowProperty(this, 1, s[0]);
//			i.sendWindowProperty(this, 2, s[1]);
//			s = Util.retractIntToShort(maxEnergy);
//			i.sendWindowProperty(this, 3, s[0]);
//			i.sendWindowProperty(this, 4, s[1]);
//		}
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

	public static class TagSlot extends Slot {
		private CableConfig config;
		private static IInventory EMPTY = new InventoryBasic("EMPTY", false, 0);

		public TagSlot(CableConfig config, int index, int xPosition, int yPosition) {
			super(EMPTY, index, xPosition, yPosition);
			this.config = config;
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return false;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return !stack.isEmpty();
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}

		@Override
		public void putStack(ItemStack stack) {
			synchronized (config.getWhitelist()) {
				if (!stack.isEmpty()) {
					stack.setCount(1);
				}
				config.getWhitelist()[getSlotIndex()] = stack;
				onSlotChanged();
			}
		}

		@Override
		public ItemStack getStack() {
			return config.getWhitelist()[getSlotIndex()];
		}

		@Override
		public ItemStack decrStackSize(int amount) {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean isHere(IInventory inv, int slotIn) {
			return false;
		}
	}
}
