package com.pinball3d.zone.sphinx;

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
}
