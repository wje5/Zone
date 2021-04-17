package com.pinball3d.zone.sphinx.log.component;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNeedNetworkInfo;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkInfo;
import com.pinball3d.zone.util.Util;

public class LogComponentNeedNetwork extends LogComponent {
	private SerialNumber serial;

	public LogComponentNeedNetwork(SerialNumber serial) {
		super(Type.NEEDNETWORK);
		this.serial = serial;
	}

	public SerialNumber getSerial() {
		return serial;
	}

	@Override
	public void onClick() {
		super.onClick();
		IHasSubscreen root = Util.getRoot();
		if (!serial.isDead()) {
			if (serial.equals(SerialNumber.CENTER)) {
				root.putScreen(new SubscreenNetworkInfo(root));
			} else {
				root.putScreen(new SubscreenNeedNetworkInfo(root, serial));
			}
		}
	}

	@Override
	public int getColor() {
		return serial.isDead() ? 0xFFFC3D3D : 0xFF3AFAFD;
	}

	@Override
	public String toString() {
		return serial.toString();
	}
}
