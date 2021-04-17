package com.pinball3d.zone.sphinx.log.component;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkInfo;
import com.pinball3d.zone.util.Util;

public class LogComponentNetwork extends LogComponent {
	public LogComponentNetwork() {
		super(Type.NETWORK);
	}

	@Override
	public void onClick() {
		super.onClick();
		IHasSubscreen root = Util.getRoot();
		root.putScreen(new SubscreenNetworkInfo(root));
	}

	@Override
	public String toString() {
		return ConnectHelperClient.getInstance().getName();
	}
}
