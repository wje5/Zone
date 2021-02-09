package com.pinball3d.zone.sphinx.container;

import java.util.HashSet;
import java.util.Set;

import com.pinball3d.zone.gui.GuiContainerZone;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.ConnectionHelper.Type;

public abstract class GuiContainerNetworkBase extends GuiContainerZone {
	public GuiContainerNetworkBase(ContainerNetworkBase container) {
		super(container);
	}

	@Override
	protected void init() {
		super.init();
		sendReq();
	}

	public abstract void sendReq();

	public Set<Type> getDataTypes() {
		Set<Type> set = new HashSet<Type>();
		subscreens.forEach(e -> {
			set.addAll(e.getDataTypes());
		});
		return set;
	}

	@Override
	public void onGuiClosed() {
		ConnectHelperClient.getInstance().disconnect();
		super.onGuiClosed();
	}
}
