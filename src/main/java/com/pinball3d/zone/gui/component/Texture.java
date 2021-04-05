package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class Texture extends Component {
	protected ResourceLocation texture;
	protected int u, v, uWidth, vHeight;
	protected Runnable event;

	public Texture(IHasComponents parent, int x, int y, ResourceLocation texture, int uWidth, int vHeight,
			float scale) {
		this(parent, x, y, texture, 0, 0, uWidth, vHeight, scale);
	}

	public Texture(IHasComponents parent, int x, int y, ResourceLocation texture, int u, int v, int uWidth, int vHeight,
			float scale) {
		this(parent, x, y, texture, (int) (scale * uWidth), (int) (scale * vHeight), u, v, uWidth, vHeight);
	}

	public Texture(IHasComponents parent, int x, int y, ResourceLocation texture, int width, int height, int u, int v,
			int uWidth, int vHeight) {
		super(parent, x, y, width, height);
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Util.drawTexture(texture, 0, 0, width, height, u, v, uWidth, vHeight);
	}
}
