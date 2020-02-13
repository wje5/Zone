package com.pinball3d.zone.gui;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEDrainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDrainer extends Container {
	private IItemHandler energy;
	protected TEDrainer tileEntity;

	public ContainerDrainer(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEDrainer) tileEntity;
		energy = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		addSlotToContainer(new SlotItemHandler(energy, 0, 80, 30) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == ItemLoader.energy;
			}
		});
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 74 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 132));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
