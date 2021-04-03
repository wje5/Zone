package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.HyperTextButton;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageDisconnect;
import com.pinball3d.zone.network.MessageTerminalDisconnect;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.container.GuiContainerNeedNetwork;
import com.pinball3d.zone.sphinx.container.GuiContainerTerminal;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenCheckConnectedNetwork extends Subscreen {
	public String name;

	public SubscreenCheckConnectedNetwork(IHasSubscreen parent, String name) {
		this(parent, name, getDisplayWidth() / 2 - 82, getDisplayHeight() / 2 - 17);
	}

	public SubscreenCheckConnectedNetwork(IHasSubscreen parent, String name, int x, int y) {
		super(parent, x, y, 165, 35, false);
		this.name = name;
		addComponent(new HyperTextButton(this, 35, 24, I18n.format("sphinx.info"), () -> {
			parent.removeScreen(SubscreenCheckConnectedNetwork.this);
			parent.putScreen(new SubscreenNetworkInfo(parent));
		}));
		addComponent(new HyperTextButton(this, 70, 24, I18n.format("sphinx.disconnect"), () -> {
			if (parent instanceof GuiContainerTerminal) {
				parent.removeScreen(SubscreenCheckConnectedNetwork.this);
				NetworkHandler.instance.sendToServer(new MessageTerminalDisconnect(mc.player));
			} else if (parent instanceof GuiContainerNeedNetwork) {
				NetworkHandler.instance
						.sendToServer(MessageDisconnect.newMessage(ConnectHelperClient.getInstance().getNetworkPos(),
								ConnectHelperClient.getInstance().getNeedNetworkSerial()));
				parent.removeScreen(SubscreenCheckConnectedNetwork.this);
			}
		}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(0, 0, width, height, 0xAF282828);
		Util.drawTexture(ICONS, 8, 8, 0, 16, 32, 25, 0.5F);
		Util.renderGlowString(name, 35, 4);
	}
}
