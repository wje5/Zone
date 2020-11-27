package com.pinball3d.zone.sphinx;

import java.util.UUID;

import com.pinball3d.zone.network.ConnectHelperClient;

import net.minecraft.nbt.NBTTagCompound;

public class ScreenNeedNetwork extends ScreenSphinxBase {
	public final WorldPos pos;
	public NBTTagCompound data;

	public ScreenNeedNetwork(WorldPos pos) {
		this.pos = pos;
	}

	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(new ButtonNetworkConfig(this, width - 10, 2, () -> {
			subscreens.push(new SubscreenNetworkConfig((ScreenNeedNetwork) mc.currentScreen, pos));
		}, false));
	}

	@Override
	public boolean isConnected() {
		return ConnectHelperClient.instance.isConnected;
	}

	@Override
	public WorldPos getNetwork() {
		return ConnectHelperClient.instance.networkPos;
	}

	@Override
	public boolean canOpen() {
		return true;
	}

	@Override
	public UUID getNetworkUUID() {
		return ConnectHelperClient.instance.network;
	}

	@Override
	public void resetNetwork() {
//		tileentity.deleteNetwork(); TODO
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {

	}
}
