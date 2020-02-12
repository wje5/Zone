package com.pinball3d.zone.tileentity;

public class TEDrainer extends ZoneMachine {
	public TEDrainer() {
		super(1);
	}

	@Override
	public void update() {
		super.update();
		if (tick >= 1200) {
			tick -= 1200;
			addEnergy(1);
		}
	}
}
