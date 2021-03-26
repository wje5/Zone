package com.pinball3d.zone.sphinx.component;

import com.pinball3d.zone.gui.IHasComponents;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageDisconnect;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.map.Pointer;
import com.pinball3d.zone.sphinx.map.PointerNeedNetwork;

import net.minecraft.util.ResourceLocation;

public class ButtonUnitDelete extends TexturedButton implements IUnitButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("zone:textures/gui/sphinx/icons.png");
	private Pointer unit;

	public ButtonUnitDelete(IHasComponents parent, Pointer unit, int x, int y) {
		super(parent, x, y, TEXTURE, 148, 68, 32, 32, 0.25F, () -> {
			NetworkHandler.instance.sendToServer(MessageDisconnect.newMessage(
					ConnectHelperClient.getInstance().getNetworkPos(), (((PointerNeedNetwork) unit).serial)));
		});
		this.unit = unit;
	}

	@Override
	public Pointer getUnit() {
		return unit;
	}
}