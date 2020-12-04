package com.pinball3d.zone.sphinx;

import net.minecraft.util.ResourceLocation;

public class ButtonNetworkConfig extends TexturedButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public ButtonNetworkConfig(IParent parent, int x, int y, Runnable onClick, boolean flag) {
		super(parent, x, y, TEXTURE, 0, 16, 32, 32, 0.25F, onClick);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
//		if (flag) {
//			connected = ((ScreenTerminal) parent).isConnected();
//		} else {
//			INeedNetwork te = ((ScreenNeedNetwork) parent).getNeedNetworkTileEntity();
//			connected = te.getNetwork() != null && te.isConnected();
//		}
		boolean flag = false;
		if (parent instanceof ScreenSphinxBase) {
			flag = ((ScreenSphinxBase) parent).isOnline();
		}
		Util.drawTexture(texture, x, y, width, flag ? 6 : 8, flag ? 0 : 84, flag ? 16 : 0, uWidth, flag ? 26 : 32);
	}
}
