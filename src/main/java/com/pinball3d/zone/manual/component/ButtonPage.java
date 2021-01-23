package com.pinball3d.zone.manual.component;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.component.TexturedButton;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class ButtonPage extends TexturedButton {
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/manual.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("zone:textures/gui/manual_2.png");
	private final boolean flag;

	public ButtonPage(IHasComponents parent, int x, int y, boolean flag, Runnable onClick) {
		super(parent, x, y, TEXTURE, 146, 0, 10, 13, 1.0F, onClick);
		this.flag = flag;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		if ((enable == null || enable.getAsBoolean())
				&& (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height)) {
			Util.drawTexture(flag ? TEXTURE : TEXTURE2, x, y, width, height, u, v, uWidth, vHeight);
		}
	}
}
