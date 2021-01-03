package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerSphinxLoad extends ContainerNetworkBase {
	protected WorldPos controller;

	public ContainerSphinxLoad(EntityPlayer player, WorldPos controller) {
		super(player);
	}

	public WorldPos getControllerPos() {
		return controller;
	}
}
