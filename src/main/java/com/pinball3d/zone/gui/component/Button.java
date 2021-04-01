package com.pinball3d.zone.gui.component;

import com.pinball3d.zone.gui.IHasComponents;

public class Button extends Component {
	protected Runnable event;

	public Button(IHasComponents parent, int x, int y, int width, int height, Runnable onClick) {
		super(parent, x, y, width, height);
		event = onClick;
	}

	@Override
	public boolean onLeftClick(int x, int y) {
		if (super.onLeftClick(x, y)) {
			return true;
		}
		if (!isEnable()) {
			return false;
		}
		if (event != null) {
			event.run();
		}
		return true;
	}
}