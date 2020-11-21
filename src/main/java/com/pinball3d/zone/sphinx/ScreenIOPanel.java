package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageOpenIOPanelGui;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.tileentity.INeedNetwork;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ScreenIOPanel extends ScreenNeedNetwork {
	public ScreenIOPanel(INeedNetwork te) {
		super(te);
	}

	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(new TexturedButton(this, width - 20, 2, TEXTURE, 32, 68, 32, 32, 0.25F, () -> {
			BlockPos pos = ((TileEntity) tileentity).getPos();
			NetworkHandler.instance
					.sendToServer(new MessageOpenIOPanelGui(mc.player, pos.getX(), pos.getY(), pos.getZ()));
		}));
	}
}
