package com.pinball3d.zone.inventory;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.pinball3d.zone.sphinx.HugeItemStack;
import com.pinball3d.zone.sphinx.StorageWrapper;
import com.pinball3d.zone.sphinx.SubscreenIOPanelRequest;
import com.pinball3d.zone.tileentity.TEIOPanel;

import net.minecraft.client.Minecraft;
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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIOPanel extends Container {
	public IItemHandler inv, global;
	public TEIOPanel tileEntity;
	protected StorageWrapper data = new StorageWrapper();
	public int page = 1;
	public int maxPage = 1;
	public int[] list = new int[36];
	public String search = "";
	public int x, y;

	public ContainerIOPanel(EntityPlayer player, TileEntity tileEntity) {
		for (int i = 0; i < 36; i++) {
			list[i] = 1;
		}
		this.tileEntity = (TEIOPanel) tileEntity;
		inv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		global = new ItemStackHandler(36);
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new SlotItemHandler(inv, j + i * 9, 54 + j * 19, 10 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			for (int j = 0; j < 4; ++j) {
				addSlotToContainer(new SlotItemHandler(global, j + i * 4, j * 19 - 38, 29 + i * 19) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						if (tileEntity.getWorld().isRemote) {
							Minecraft mc = Minecraft.getMinecraft();
							if (mc.currentScreen instanceof GuiContainerIOPanel) {
								GuiContainerIOPanel container = (GuiContainerIOPanel) mc.currentScreen;
								int offsetX = container.width / 2 - 184, offsetY = (container.height - 213) / 2;
								container.putScreen(new SubscreenIOPanelRequest(container, x - offsetX, y - offsetY,
										getStack(), list[slotNumber - 54]));
							}
						}
						return false;
					}
				});
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 54 + j * 19, 129 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 54 + i * 19, 189));
		}
	}

	public void setData(StorageWrapper data) {
		this.data = data;
		updateGlobalStorges();
	}

	public void setSearch(String search) {
		this.search = search;
		updateGlobalStorges();
	}

	public void updateGlobalStorges() {
		for (int i = 0; i < 36; i++) {
			global.extractItem(i, 64, false);
		}
		if (data == null) {
			return;
		}
		int index = 0;
		int offset = (page - 1) * 36;

		Set<HugeItemStack> storges = data.storges;
		if (!search.isEmpty()) {
			storges = new TreeSet<HugeItemStack>(StorageWrapper.hugeStackComparator);
			Iterator<HugeItemStack> it = data.storges.iterator();
			while (it.hasNext()) {
				HugeItemStack hugestack = it.next();
				if (hugestack.stack.getItem().getRegistryName().getResourcePath().contains(search)
						|| hugestack.stack.getDisplayName().contains(search)) {
					storges.add(hugestack);
				}
			}
		}
		Iterator<HugeItemStack> it = storges.iterator();
		while (it.hasNext()) {
			if (offset > 0) {
				offset--;
				it.next();
				continue;
			}
			ItemStack stack = it.next().stack.copy();
			stack.setCount(1);
			if (index < 36) {
				global.insertItem(index, stack, false);
			}
			index++;
		}
		Set<ItemStack> other = data.other;
		if (!search.isEmpty()) {
			other = new TreeSet<ItemStack>(StorageWrapper.stackComparator);
			Iterator<ItemStack> it2 = data.other.iterator();
			while (it2.hasNext()) {
				ItemStack stack = it2.next();
				if (stack.getItem().getRegistryName().getResourcePath().contains(search)
						|| stack.getDisplayName().contains(search)) {
					other.add(stack);
				}
			}
		}
		Iterator<ItemStack> it2 = other.iterator();
		while (it2.hasNext()) {
			if (offset > 0) {
				offset--;
				it2.next();
				continue;
			}
			ItemStack stack = it2.next().copy();
			stack.setCount(1);
			if (index < 36) {
				global.insertItem(index, stack, false);
			}
			index++;
		}
		maxPage = (storges.size() + other.size() - 1) / 36 + 1;
		if (page > maxPage) {
			page = maxPage;
		}
		if (page < 1) {
			page = 1;
		}
	}

	public void updateAmountList() {
		for (int i = 0; i < 36; i++) {
			list[i] = 1;
		}
		int offset = (page - 1) * 36;
		Set<HugeItemStack> storges = data.storges;
		if (!search.isEmpty()) {
			storges = new TreeSet<HugeItemStack>(StorageWrapper.hugeStackComparator);
			Iterator<HugeItemStack> it = data.storges.iterator();
			while (it.hasNext()) {
				HugeItemStack hugestack = it.next();
				if (hugestack.stack.getItem().getRegistryName().getResourcePath().contains(search)
						|| hugestack.stack.getDisplayName().contains(search)) {
					storges.add(hugestack);
				}
			}
		}
		Iterator<HugeItemStack> it = storges.iterator();
		int i = 0;
		while (it.hasNext()) {
			HugeItemStack hugestack = it.next();
			if (offset > 0) {
				offset--;
				continue;
			}
			if (i >= 36) {
				break;
			}
			int count = hugestack.count;
			if (count != 1) {
				list[i] = count;
			}
			i++;
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		updateAmountList();
		for (IContainerListener j : listeners) {
			j.sendWindowProperty(this, 0, page);
			j.sendWindowProperty(this, 1, maxPage);
			for (int k = 0; k < 36; k++) {
				j.sendWindowProperty(this, k + 2, list[k]);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		switch (id) {
		case 0:
			page = data;
			return;
		case 1:
			maxPage = data;
			return;
		}
		list[id - 2] = data;
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
				isMerged = mergeItemStack(newStack, 81, 90, false);
			}
		} else if (index >= 81) {
			isMerged = mergeItemStack(newStack, 0, 81, false);
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
