package com.pinball3d.zone.sphinx;

import net.minecraft.util.ResourceLocation;

public class ButtonUnitInfo extends TexturedButton implements IUnitButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Pointer unit;

	public ButtonUnitInfo(IParent parent, Pointer unit, int x, int y) {
		super(parent, x, y, TEXTURE, 116, 68, 32, 32, 0.25F, () -> {
			if (unit instanceof PointerProcessingCenter) {
				parent.putScreen(new SubscreenNetworkInfo(parent, ((PointerProcessingCenter) unit).pos));
			} else if (unit instanceof PointerNeedNetwork) {
				parent.putScreen(new SubscreenNeedNetworkInfo(parent, ((PointerNeedNetwork) unit).pos));
			}
		});
		this.unit = unit;
	}

	@Override
	public Pointer getUnit() {
		return unit;
	}
}
