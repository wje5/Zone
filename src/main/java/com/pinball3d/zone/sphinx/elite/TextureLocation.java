package com.pinball3d.zone.sphinx.elite;

import net.minecraft.util.ResourceLocation;

public class TextureLocation {
	public final ResourceLocation location;
	public final float u, v, uWidth, vHeight;

	public TextureLocation(ResourceLocation location, float u, float v, float uWidth, float vHeight) {
		this.location = location;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
	}
}
