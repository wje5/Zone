package com.pinball3d.zone.sphinx.map;

import java.util.ArrayList;
import java.util.List;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.component.Component;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class Pointer extends Gui {
	public BoundingBox box;
	public static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");

	public abstract void doRender(int offsetX, int offsetZ);

	public Pointer(BoundingBox box) {
		this.box = box;
	}

	public void renderThumb(int x, int z) {

	}

	public void renderThumbHuge(int x, int z) {

	}

	public List<Component> getUnitButtons(IHasComponents parent) {
		List<Component> l = new ArrayList<Component>();
		return l;
	}

	public static class BoundingBox {
		public int x, y, x2, y2;

		public BoundingBox(int x, int y, int x2, int y2) {
			this.x = x > x2 ? x2 : x;
			this.y = y > y2 ? y2 : y;
			this.x2 = x < x2 ? x2 : x;
			this.y2 = y < y2 ? y2 : y;
		}

		public boolean isInBox(int x, int y) {
			return x >= this.x && x <= this.x2 && y >= this.y && y <= this.y2;
		}

		public boolean isCollision(BoundingBox box) {
			int width = x2 - x;
			int height = y2 - y;
			int width2 = box.x2 - box.x;
			int height2 = box.y2 - box.y;
			float xCenter = x + width / 2.0F;
			float yCenter = y + height / 2.0F;
			float xCenter2 = box.x + width2 / 2.0F;
			float yCenter2 = box.y + height2 / 2.0F;
			return Math.abs(xCenter - xCenter2) <= (width + width2) / 2.0F
					&& Math.abs(yCenter - yCenter2) <= (height + height2) / 2.0F;
		}

		@Override
		public String toString() {
			return "{x:" + x + " y:" + y + " x2:" + x2 + " y2:" + y2 + "}";
		}
	}
}
