package com.pinball3d.zone.sphinx.component;

import java.util.function.BooleanSupplier;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class TexturedButton extends Component {
	protected ResourceLocation texture;
	protected int u, v, uWidth, vHeight;
	protected Runnable event;
	protected BooleanSupplier enable;

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
		this.x = x;
		this.y = y;
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		event = onClick;
	}

	public TexturedButton setEnable(BooleanSupplier enable) {
		this.enable = enable;
		return this;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (enable == null || enable.getAsBoolean()) {
			event.run();
			return true;
		}
		return false;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (enable == null || enable.getAsBoolean()) {
			Util.drawTexture(texture, x, y, width, height, u, v, uWidth, vHeight);
		}
	}
}
