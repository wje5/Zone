package com.pinball3d.zone.sphinx.component;

import java.util.function.BooleanSupplier;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Button;
import com.pinball3d.zone.util.Util;

public class ButtonNetworkConfig extends Button {
	private BooleanSupplier connect;

	public ButtonNetworkConfig(IHasComponents parent, int x, int y, Runnable onClick, BooleanSupplier connect) {
		super(parent, x, y, 15, 15, onClick);
		this.connect = connect;
	}

	@Override
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);// TODO
		Util.drawTexture(TEXTURE_4, 0, 0, connect.getAsBoolean() ? 120 : 180, 60, 60, 60, 0.25F);
	}
}
