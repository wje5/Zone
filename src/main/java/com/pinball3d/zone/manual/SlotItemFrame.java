package com.pinball3d.zone.manual;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotItemFrame extends SlotItemHandler {
	public SlotItemFrame(int xPosition, int yPosition, ItemStack stack) {
		super(new ItemStackHandler(), 0, xPosition, yPosition);
		putStack(stack);
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
