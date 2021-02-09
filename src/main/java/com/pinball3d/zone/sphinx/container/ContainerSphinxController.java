package com.pinball3d.zone.sphinx.container;

import com.pinball3d.zone.block.BlockControllerMainframe;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerSphinxController extends ContainerSphinxAdvanced {
	protected WorldPos controller;

	public ContainerSphinxController(EntityPlayer player, WorldPos controller) {
		super(player);
		this.controller = controller;
	}

	public WorldPos getControllerPos() {
		return controller;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!super.canInteractWith(playerIn)) {
			return false;
		}
		WorldPos pos = BlockControllerMainframe.getProcessingCenterPos(controller);
		if (pos.isOrigin()) {
			return false;
		}
		TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
		return te.isAdmin(playerIn);
	}
}
