package com.pinball3d.zone.sphinx.container;

import java.util.Set;

import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.component.ButtonNetworkConfig;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkConfig;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.nbt.NBTTagCompound;

public class GuiContainerNeedNetwork extends GuiContainerSphinxBase {
	public WorldPos pos;
	public NBTTagCompound data;

	public GuiContainerNeedNetwork(ContainerSphinxNeedNetwork container, WorldPos pos) {
		super(container);
		this.pos = pos;
	}

	@Override
	public void sendReq() {
		ConnectHelperClient.getInstance().requestNeedNetwork(pos, getDataTypes().toArray(new Type[] {}));
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> set = super.getDataTypes();
		set.add(Type.NETWORKUUID);
		set.add(Type.NEEDNETWORKVALIDNETWORK);
		return set;
	}

	@Override
	public void addComponents() {
		super.addComponents();
		addComponent(new ButtonNetworkConfig(this, width - 10, 2, () -> {
			subscreens.push(new SubscreenNetworkConfig(GuiContainerNeedNetwork.this, pos));
		}, () -> ConnectHelperClient.getInstance().isConnected()));
	}
}
