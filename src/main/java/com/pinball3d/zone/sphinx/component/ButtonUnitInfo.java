package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.sphinx.IHasComponents;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.map.Pointer;
import com.pinball3d.zone.sphinx.map.PointerNeedNetwork;
import com.pinball3d.zone.sphinx.map.PointerProcessingCenter;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNeedNetworkInfo;
import com.pinball3d.zone.sphinx.subscreen.SubscreenNetworkInfo;
import com.pinball3d.zone.util.Util;

import net.minecraft.util.ResourceLocation;

public class ButtonUnitInfo extends TexturedButton implements IUnitButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Pointer unit;

	public ButtonUnitInfo(IHasComponents parent, Pointer unit, int x, int y) {
		super(parent, x, y, TEXTURE, 116, 68, 32, 32, 0.25F, () -> {
			IHasSubscreen root = Util.getRoot();
			if (unit instanceof PointerProcessingCenter) {
				root.putScreen(new SubscreenNetworkInfo(root, ((PointerProcessingCenter) unit).pos));
			} else if (unit instanceof PointerNeedNetwork) {
				root.putScreen(new SubscreenNeedNetworkInfo(root, ((PointerNeedNetwork) unit).pos));
			}
		});
		this.unit = unit;
	}

	@Override
	public Pointer getUnit() {
		return unit;
	}
}
