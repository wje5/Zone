package com.pinball3d.zone.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TETerminal extends TileEntity implements ITickable {
	private EntityPlayer player;

	@Override
	public void update() {
		if (player != null) {
			BlockPos pos = getPos();
			if (player.getDistance(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F) > 16F) {
				stopWorking();
			}
		}
	}

	public boolean isWorking() {
		return player == null;
	}

	public void setWorking(EntityPlayer player) {
		this.player = player;
	}

	public void stopWorking() {
		player = null;
	}
}
