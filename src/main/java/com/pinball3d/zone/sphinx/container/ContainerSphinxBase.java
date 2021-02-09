package com.pinball3d.zone.sphinx.container;

import net.minecraft.entity.player.EntityPlayer;

public abstract class ContainerSphinxBase extends ContainerNetworkBase {
	public ContainerSphinxBase(EntityPlayer player) {
		super(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return super.canInteractWith(playerIn);
	}
}
