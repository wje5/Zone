package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.Component;
import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.util.Util;

public class RadioButton extends Component {
	protected Runnable event;
	protected boolean isChoosed;

	public RadioButton(IHasComponents parent, int x, int y, Runnable onClick) {
		super(parent, x, y, 9, 9);
		event = onClick;
	}

	public RadioButton setState(boolean flag) {
		this.isChoosed = flag;
		return this;
	}

	public boolean getState() {
		return isChoosed;
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
		if (enable != null && !enable.getAsBoolean()) {
			return;
		}
		Util.drawTexture(TEXTURE_4, getX(), getY(), isChoosed ? 120 : 60, 180, 60, 60, 0.13F);
	}
}
