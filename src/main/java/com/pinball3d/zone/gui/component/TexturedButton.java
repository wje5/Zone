package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class TexturedButton extends Button {
	protected ResourceLocation texture;
	protected int u, v, uWidth, vHeight;
	protected Runnable event;

	public TexturedButton(IHasComponents parent, int x, int y, ResourceLocation texture, int uWidth, int vHeight,
			float scale, Runnable onClick) {
		this(parent, x, y, texture, 0, 0, uWidth, vHeight, scale, onClick);
	}

	public TexturedButton(IHasComponents parent, int x, int y, ResourceLocation texture, int u, int v, int uWidth,
			int vHeight, float scale, Runnable onClick) {
		this(parent, x, y, texture, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight, onClick);
	}

	public TexturedButton(IHasComponents parent, int x, int y, ResourceLocation texture, int width, int height, int u,
			int v, int uWidth, int vHeight, Runnable onClick) {
		super(parent, x, y, width, height, onClick);
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		event = onClick;
	}

	@Override
	public void doRender(int mouseX, int mouseY, int upCut, int downCut) {
		super.doRender(mouseX, mouseY, upCut, downCut);// TODO
		if (isEnable()) {
			Util.drawTexture(texture, 0, 0, width, height, u, v, uWidth, vHeight);
		}
	}
}
