package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.SoundUtil;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;

public class TEDrainer extends ZoneTieredMachine {
	protected int tick;

	public TEDrainer() {
		super();
	}

	public TEDrainer(Tier tier) {
		super(tier, 8000);
	}

	@Override
	public void update() {
		super.update();
		tick++;
		if (world.isRemote) {
			if (tick % 200 == 0 && !ConfigLoader.disableMachineSound) {
				world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
						SoundUtil.getSoundEventFromId(1), SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
			return;
		}
		energy.receiveEnergy(10 * getTier().getMultiple(), false);
	}

	@Override
	public boolean activeOutput(EnumFacing facing) {
		return true;
	}

	@Override
	public boolean canReceiveEnergy(EnumFacing facing) {
		return false;
	}
}
