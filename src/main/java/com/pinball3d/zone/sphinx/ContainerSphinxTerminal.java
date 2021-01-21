package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSphinxTerminal extends ContainerSphinxAdvanced {
	public ContainerSphinxTerminal(EntityPlayer player) {
		super(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!super.canInteractWith(playerIn)) {
			return false;
		}
		ItemStack stack = playerIn.getHeldItemMainhand();
		ItemStack stack2 = playerIn.getHeldItemOffhand();
		return (stack.getItem() == ItemLoader.terminal && stack.getItemDamage() == 0)
				|| (stack2.getItem() == ItemLoader.terminal && stack2.getItemDamage() == 0);
	}
}
