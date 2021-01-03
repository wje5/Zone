package com.pinball3d.zone.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public abstract class ContainerZone extends Container {
	public ContainerZone(EntityPlayer player) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
