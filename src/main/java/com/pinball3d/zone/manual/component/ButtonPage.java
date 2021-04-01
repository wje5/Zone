package com.pinball3d.zone.manual.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.component.Button;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class ButtonPage extends Button {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/manual.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("zone:textures/gui/manual_2.png");
	private final boolean flag;

	public ButtonPage(IHasComponents parent, int x, int y, boolean flag, Runnable onClick) {
		super(parent, x, y, 10, 13, onClick);
		this.flag = flag;
	}

	@Override
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		if (isHover(mouseX, mouseY)) {// TODO
			Util.drawTexture(flag ? TEXTURE : TEXTURE2, 0, 0, 146, 0, 10, 13, 1.0F);
		}
	}
}
