package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class Slider extends Component {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	public int scrollingDistance;
	public Runnable onChange;

	public Slider(IHasComponents parent, int x, int y, int width) {
		super(parent, x, y + 1, width, 5);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		if (enable != null && !enable.getAsBoolean()) {
			return;
		}
		Gui.drawRect(getX() + 2, getY() - 1, getX() + width - 2, getY(), 0xFF1ECCDE);
		Util.drawTexture(TEXTURE, getX() + scrollingDistance, getY() + 1, 102, 32, 5, 5, 1.0F);
	}

	@Override
	public boolean onDrag(int mouseX, int mouseY, int moveX, int moveY) {
		if (super.onDrag(mouseX, mouseY, moveX, moveY)) {
			return true;
		}
		if (enable != null && !enable.getAsBoolean()) {
			return false;
		}
		scrollingDistance = mouseX - 2;
		scrollingDistance = scrollingDistance < 0 ? 0 : scrollingDistance;
		scrollingDistance = scrollingDistance > width - 5 ? width - 5 : scrollingDistance;
		onChange.run();
		return true;
	}

	public float get() {
		return 1.0F * scrollingDistance / (width - 5);
	}

	public void set(float f) {
		scrollingDistance = (int) (f * (width - 5));
	}

	public void setOnChange(Runnable onChange) {
		this.onChange = onChange;
	}
}
