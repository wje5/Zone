package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.util.Util;

public class RadioButton extends Component {
	protected Runnable event;
	protected boolean isChoosed;

	public RadioButton(IHasComponents parent, int x, int y, Runnable onClick) {
		super(parent, x, y, 7, 7);
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
		Util.drawTexture(ICONS_4, 0, 0, isChoosed ? 120 : 60, 180, 60, 60, 0.13F);
	}
}
