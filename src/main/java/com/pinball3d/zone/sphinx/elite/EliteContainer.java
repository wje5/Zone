package com.pinball3d.zone.sphinx.elite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EliteContainer extends Container {
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
