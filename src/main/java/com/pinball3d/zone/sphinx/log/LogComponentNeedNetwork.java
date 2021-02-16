package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.sphinx.SerialNumber;

public class LogComponentNeedNetwork extends LogComponent {
	private SerialNumber serial;

	public LogComponentNeedNetwork(SerialNumber serial) {
		super(Type.NEEDNETWORK);
		this.serial = serial;
	}

	@Override
	public String toString() {
		return serial.toString();
	}
}
