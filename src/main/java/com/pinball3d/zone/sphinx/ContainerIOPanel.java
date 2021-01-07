package com.pinball3d.zone.sphinx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.pinball3d.zone.network.MessageUpdateContainerIOPanel;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.subscreen.SubscreenIOPanelRequest;
import com.pinball3d.zone.tileentity.TEIOPanel;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerIOPanel extends ContainerSphinxBase {
	public IItemHandler inv, global;
	public TEIOPanel tileEntity;
	protected StorageWrapper data = new StorageWrapper();
	public int page = 1;
	public int maxPage = 1;
	public int[] list = new int[36];
	public String search = "";
	public int x, y, offsetedX, offsetedY;
	public EntityPlayer player;

	public ContainerIOPanel(EntityPlayer player, TileEntity tileEntity) {
		super(player);
		this.player = player;
		for (int i = 0; i < 36; i++) {
			list[i] = 1;
		}
		this.tileEntity = (TEIOPanel) tileEntity;
		inv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		global = new ItemStackHandler(36);
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new SlotItemHandler(inv, j + i * 9, 100 + j * 19, 10 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			for (int j = 0; j < 4; ++j) {
				addSlotToContainer(new SlotItemHandler(global, j + i * 4, j * 19 + 8, 29 + i * 19) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						if (tileEntity.getWorld().isRemote) {
							Minecraft mc = Minecraft.getMinecraft();
							if (mc.currentScreen instanceof GuiContainerIOPanel) {
								GuiContainerIOPanel panel = (GuiContainerIOPanel) mc.currentScreen;
								int offsetX = panel.width / 2 - 184, offsetY = (panel.height - 213) / 2;
								int slotX = (slotNumber - 54) % 4 * 19 + 7;
								int slotY = (slotNumber - 54) / 4 * 19 + 29;
								if (x - offsetX >= slotX && x - offsetX <= slotX + 16 && y - offsetY >= slotY
										&& y - offsetY <= slotY + 16) {
									SubscreenIOPanelRequest s = new SubscreenIOPanelRequest(panel, offsetedX - offsetX,
											offsetedY - offsetY, getStack(), list[slotNumber - 54]);
									if (Keyboard.isKeyDown(
											Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
										s.max();
									}
									panel.putScreen(s);
								}
							}
						}
						return false;
					}
				});
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 100 + j * 19, 129 + i * 19));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 100 + i * 19, 189));
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
		int index = 0;
		int offset = (page - 1) * 36;
		StorageWrapper s = Util.search(data, search);
		Iterator<HugeItemStack> it = s.storges.iterator();
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
		Iterator<ItemStack> it2 = s.other.iterator();
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
		maxPage = (s.getSize() - 1) / 36 + 1;
		if (page > maxPage) {
			page = maxPage;
		}
		if (page < 1) {
			page = 1;
		}
		updateAmountList();
	}

	public void updateAmountList() {
		for (int i = 0; i < 36; i++) {
			list[i] = 1;
		}
		int offset = (page - 1) * 36;
		Set<HugeItemStack> storges = Util.search(data, search).storges;
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
		int[] data = new int[list.length + 2];
		data[0] = page;
		data[1] = maxPage;
		for (int j = 0; j < list.length; j++) {
			data[j + 2] = list[j];
		}
		NetworkHandler.instance.sendTo(new MessageUpdateContainerIOPanel(data), (EntityPlayerMP) player);
	}

	public void setData(int[] data) {
		page = data[0];
		maxPage = data[1];
		list = Arrays.copyOfRange(data, 2, data.length);
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
			isMerged = mergeItemStack(newStack, 90, 126, true);
		} else if (index >= 54 && index < 90) {
			return ItemStack.EMPTY;
		} else if (index >= 90 && index < 117) {
			isMerged = mergeItemStack(newStack, 0, 54, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 117, 126, true);
			}
		} else {
			isMerged = mergeItemStack(newStack, 0, 54, false);
			if (!isMerged) {
				isMerged = mergeItemStack(newStack, 90, 117, true);
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
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
