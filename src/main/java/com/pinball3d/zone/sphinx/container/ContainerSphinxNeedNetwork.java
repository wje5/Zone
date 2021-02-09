package com.pinball3d.zone.sphinx.container;

import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerSphinxNeedNetwork extends ContainerSphinxBase {
	public INeedNetwork tileEntity;

	public ContainerSphinxNeedNetwork(EntityPlayer player, INeedNetwork tileEntity) {
		super(player);
		this.tileEntity = tileEntity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (!super.canInteractWith(playerIn)) {
			return false;
		}
		UUID network = tileEntity.getNetwork();
		if (network == null) {
			return true;
		}
		WorldPos pos = GlobalNetworkData.getData(playerIn.world).getNetwork(network);
		if (pos.isOrigin()) {
			return true;
		}
		TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
		return te.isUser(playerIn);
	}
}
