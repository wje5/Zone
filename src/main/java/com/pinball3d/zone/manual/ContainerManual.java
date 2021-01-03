package com.pinball3d.zone.manual;

import com.pinball3d.zone.gui.ContainerZone;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerManual extends ContainerZone {
	public ContainerManual(EntityPlayer player) {
		super(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return super.canInteractWith(playerIn) && (playerIn.getHeldItemMainhand().getItem() == ItemLoader.manual
				|| playerIn.getHeldItemOffhand().getItem() == ItemLoader.manual);
	}
}
