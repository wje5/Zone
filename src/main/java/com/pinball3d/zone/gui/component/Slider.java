package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;

public class Slider extends Component {
	public int scrollingDistance;
	public Runnable onChange;

	public Slider(IHasComponents parent, int x, int y, int width) {
		super(parent, x, y + 1, width, 5);
	}

	@Override
	public void doRender(int mouseX, int mouseY) {
		super.doRender(mouseX, mouseY);
		Gui.drawRect(2, -1, width - 2, 0, 0xFF1ECCDE);
		Util.drawTexture(ICONS, scrollingDistance, 1, 102, 32, 5, 5, 1.0F);
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
