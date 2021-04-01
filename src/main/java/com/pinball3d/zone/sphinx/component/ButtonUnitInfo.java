package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.sphinx.map.Pointer;
import com.pinball3d.zone.sphinx.map.PointerNeedNetwork;
import com.pinball3d.zone.sphinx.map.PointerProcessingCenter;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNeedNetworkInfo;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkInfo;
import com.pinball3d.zone.util.Util;

public class ButtonUnitInfo extends TexturedButton implements IUnitButton {
	private Pointer unit;

	public ButtonUnitInfo(IHasComponents parent, Pointer unit, int x, int y) {
		super(parent, x, y, ICONS, 116, 68, 32, 32, 0.25F, () -> {
			IHasSubscreen root = Util.getRoot();
			if (unit instanceof PointerProcessingCenter) {
				root.putScreen(new SubscreenNetworkInfo(root));
			} else if (unit instanceof PointerNeedNetwork) {
				root.putScreen(new SubscreenNeedNetworkInfo(root, ((PointerNeedNetwork) unit).serial));
			}
		});
		this.unit = unit;
	}

	@Override
	public Pointer getUnit() {
		return unit;
	}
}
