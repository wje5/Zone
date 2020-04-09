package com.pinball3d.zone.sphinx;

import net.minecraft.util.ResourceLocation;

public class TexturedButton extends Component {
	protected ResourceLocation texture;
	protected int width, height, u, v, uWidth, vHeight;
	protected Runnable event;

	public TexturedButton(IParent parent, int x, int y, ResourceLocation texture, int uWidth, int vHeight, float scale,
			Runnable onClick) {
		this(parent, x, y, texture, 0, 0, uWidth, vHeight, scale, onClick);
	}

	public TexturedButton(IParent parent, int x, int y, ResourceLocation texture, int u, int v, int uWidth, int vHeight,
			float scale, Runnable onClick) {
		this(parent, x, y, texture, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight, onClick);
	}

	public TexturedButton(IParent parent, int x, int y, ResourceLocation texture, int width, int height, int u, int v,
			int uWidth, int vHeight, Runnable onClick) {
		super(parent, x, y, width, height);
		this.x = x;
		this.y = y;
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		event = onClick;
	}

	@Override
	public void onLeftClick(int x, int y) {
		super.onLeftClick(x, y);
		event.run();
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawTexture(texture, x, y, width, height, u, v, uWidth, vHeight);
	}
}
