package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class TexturedButton extends Component {
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
		super(parent, x, y, width, height);
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		event = onClick;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		event.run();
		return true;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (enable == null || enable.getAsBoolean()) {
			Util.drawTexture(texture, getX(), getY(), width, height, u, v, uWidth, vHeight);
		}
	}
}
