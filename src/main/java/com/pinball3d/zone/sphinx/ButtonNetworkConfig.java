package com.pinball3d.zone.sphinx;

import net.minecraft.util.ResourceLocation;

public class ButtonNetworkConfig extends TexturedButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public ButtonNetworkConfig(IParent parent, int x, int y, Runnable onClick, boolean flag) {
		super(parent, x, y, TEXTURE, 0, 16, 32, 32, 0.25F, onClick);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		boolean flag = ((ScreenSphinxBase) parent).isConnected();
		Util.drawTexture(texture, x, y, width, flag ? 6 : 8, flag ? 0 : 84, flag ? 16 : 0, uWidth, flag ? 26 : 32);
	}
}
