package com.pinball3d.zone.sphinx;

import com.pinball3d.zone.network.MessageDeleteNeedNetworkUnit;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.util.ResourceLocation;

public class ButtonUnitDelete extends TexturedButton implements IUnitButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Pointer unit;

	public ButtonUnitDelete(IParent parent, Pointer unit, int x, int y) {
		super(parent, x, y, TEXTURE, 148, 68, 32, 32, 0.25F, new Runnable() {
			@Override
			public void run() {
				WorldPos pos = ((PointerNeedNetwork) unit).pos;
				NetworkHandler.instance.sendToServer(new MessageDeleteNeedNetworkUnit(pos));
			}
		});
		this.unit = unit;
	}

	@Override
	public Pointer getUnit() {
		return unit;
	}
}