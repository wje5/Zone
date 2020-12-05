package com.pinball3d.zone.sphinx;

import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.network.MessageConnectionNeedNetworkRequest;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.nbt.NBTTagCompound;

public class ScreenNeedNetwork extends ScreenSphinxBase {
	public final WorldPos pos;
	public NBTTagCompound data;

	public ScreenNeedNetwork(WorldPos pos) {
		this.pos = pos;
	}

	@Override
	public void init() {
		NetworkHandler.instance.sendToServer(
				new MessageConnectionNeedNetworkRequest(mc.player, pos, getDataTypes().toArray(new Type[] {})));
	}

	@Override
	public List<Type> getDataTypes() {
		List<Type> list = super.getDataTypes();
		list.add(Type.NETWORKUUID);
		list.add(Type.NETWORKPOS);
		list.add(Type.NEEDNETWORKVALIDNETWORK);
		return list;
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
		return ConnectHelperClient.getInstance().isConnected();
	}

	@Override
	public boolean canOpen() {
		return true;
	}

	@Override
	public UUID getNetworkUUID() {
		return ConnectHelperClient.getInstance().getNetwork();
	}

	@Override
	public void resetNetwork() {
//		tileentity.deleteNetwork(); TODO
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {

	}
}
