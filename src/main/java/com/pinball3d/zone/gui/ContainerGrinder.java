package com.pinball3d.zone.gui;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEGrinder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerGrinder extends Container {
	private IItemHandler energy, input, output;
	protected TEGrinder tileEntity;

	public ContainerGrinder(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEGrinder) tileEntity;
		energy = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.WEST);
		input = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		output = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		addSlotToContainer(new SlotItemHandler(energy, 0, 56, 53) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == ItemLoader.energy;
			}
		});
		addSlotToContainer(new SlotItemHandler(input, 0, 56, 17));
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
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
