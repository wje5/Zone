package com.pinball3d.zone.sphinx;

import net.minecraft.util.ResourceLocation;

public class ButtonNetworkConfig extends TexturedButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private boolean flag;

	public ButtonNetworkConfig(IParent parent, int x, int y, Runnable onClick, boolean flag) {
		super(parent, x, y, TEXTURE, 0, 16, 32,
				(flag ? ((ScreenTerminal) parent).isConnected() : ((ScreenNeedNetwork) parent).isConnected()) ? 26 : 32,
				0.25F, onClick);
		this.flag = flag;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		boolean connected;
		if (flag) {
			connected = ((ScreenTerminal) parent).isConnected();
		} else {
			connected = ((ScreenNeedNetwork) parent).isConnected();
		}
		Util.drawTexture(texture, x, y, width, connected ? 6 : 8, connected ? 0 : 84, connected ? 16 : 0, uWidth,
				connected ? 26 : 32);
	}
}
