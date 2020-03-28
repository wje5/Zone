package com.pinball3d.zone.sphinx;

import net.minecraft.util.ResourceLocation;

public class TexturedButton {
	public int x, y;
	private ResourceLocation texture;
	private int width, height, u, v, uWidth, vHeight;
	private Runnable event;

	public TexturedButton(int x, int y, ResourceLocation texture, int uWidth, int vHeight, float scale,
			Runnable onClick) {

	}

	public TexturedButton(int x, int y, ResourceLocation texture, int u, int v, int uWidth, int vHeight, float scale,
			Runnable onClick) {
		this(x, y, texture, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight, onClick);
	}

	public TexturedButton(int x, int y, ResourceLocation texture, int width, int height, int u, int v, int uWidth,
			int vHeight, Runnable onClick) {
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

	public void onLeftClick() {
		event.run();
	}

	public void onRightClick() {

	}

	public void doRender() {
		Util.drawTexture(texture, x, y, width, height, u, v, uWidth, vHeight);
	}

	public void onClickScreen(int x, int y, boolean isLeft) {
		if (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height) {
			if (isLeft) {
				onLeftClick();
			} else {
				onRightClick();
			}
		}
	}
}
