package com.pinball3d.zone.sphinx.component;

import java.util.function.BooleanSupplier;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class ButtonNetworkConfig extends TexturedButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	private BooleanSupplier connect;

	public ButtonNetworkConfig(IHasComponents parent, int x, int y, Runnable onClick, BooleanSupplier connect) {
		super(parent, x, y, TEXTURE, 0, 16, 32, 32, 0.25F, onClick);
		this.connect = connect;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		boolean flag = connect.getAsBoolean();
		Util.drawTexture(texture, x, y, width, flag ? 6 : 8, flag ? 0 : 84, flag ? 16 : 0, uWidth, flag ? 26 : 32);
	}
}
