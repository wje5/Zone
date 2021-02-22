package com.pinball3d.zone.sphinx.component;

import java.util.function.BooleanSupplier;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class ButtonNetworkConfig extends TexturedButton {
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation("zone:textures/gui/sphinx/icons_4.png");

	private BooleanSupplier connect;

	public ButtonNetworkConfig(IHasComponents parent, int x, int y, Runnable onClick, BooleanSupplier connect) {
		super(parent, x, y, TEXTURE_4, 120, 60, 60, 60, 0.25F, onClick);
		this.connect = connect;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		boolean flag = connect.getAsBoolean();
		Util.drawTexture(texture, x, y, flag ? 120 : 180, 60, uWidth, vHeight, 0.25F);
	}
}
