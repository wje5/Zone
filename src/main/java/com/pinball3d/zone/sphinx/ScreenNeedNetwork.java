package com.pinball3d.zone.sphinx;

import java.util.UUID;

import com.pinball3d.zone.tileentity.INeedNetwork;

import net.minecraft.tileentity.TileEntity;

public class ScreenNeedNetwork extends ScreenSphinxBase {
	public INeedNetwork tileentity;

	public ScreenNeedNetwork(INeedNetwork te) {
		tileentity = te;
	}

	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(new ButtonNetworkConfig(this, width - 10, 2, () -> {
			subscreens.push(new SubscreenNetworkConfig((ScreenNeedNetwork) mc.currentScreen,
					new WorldPos((TileEntity) tileentity)));
		}, false));
	}

	@Override
	public boolean isConnected() {
		return tileentity.isConnected();
	}

	@Override
	public INeedNetwork getNeedNetworkTileEntity() {
		return tileentity;
	}

	@Override
	public WorldPos getNetwork() {
		return tileentity.getNetworkPos();
	}

	@Override
	public boolean canOpen() {
		if (tileentity == null) {
			mc.displayGuiScreen(null);
			return false;
		}
		return true;
	}

	@Override
	public UUID getNetworkUUID() {
		return tileentity.getNetwork();
	}

	@Override
	public void resetNetwork() {
		tileentity.deleteNetwork();
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {

	}
}
