package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.tileentity.INeedNetwork;

import net.minecraft.util.ResourceLocation;

public class ButtonUnitInfo extends TexturedButton implements IUnitButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Pointer unit;

	public ButtonUnitInfo(IParent parent, Pointer unit, int x, int y) {
		super(parent, x, y, TEXTURE, 116, 68, 32, 32, 0.25F, new Runnable() {
			@Override
			public void run() {
				if (unit instanceof PointerNeedNetwork) {
					parent.putScreen(new SubscreenNeedNetworkInfo(parent,
							(INeedNetwork) ((PointerNeedNetwork) unit).pos.getTileEntity()));
				}

			}
		});
		this.unit = unit;
	}

	@Override
	public Pointer getUnit() {
		return unit;
	}
}
