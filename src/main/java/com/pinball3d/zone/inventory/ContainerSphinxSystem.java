package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerSphinxSystem extends Container {
	protected TEProcessingCenter tileEntity;

	public ContainerSphinxSystem(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TEProcessingCenter) tileEntity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
