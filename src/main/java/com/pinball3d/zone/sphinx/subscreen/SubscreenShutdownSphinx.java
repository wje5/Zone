package com.pinball3d.zone.sphinx.subscreen;

import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.network.ConnectHelperClient;
import com.pinball3d.zone.network.MessageShutdownSphinx;
import com.pinball3d.zone.network.NetworkHandler;
import com.pinball3d.zone.sphinx.IHasSubscreen;
import com.pinball3d.zone.sphinx.component.TextButton;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenShutdownSphinx extends Subscreen {
	public SubscreenShutdownSphinx(IHasSubscreen parent) {
		this(parent, getDisplayWidth() / 2 - 80, getDisplayHeight() / 2 - 45);
	}

	public SubscreenShutdownSphinx(IHasSubscreen parent, int x, int y) {
		super(parent, x, y, 160, 90, false);
		addComponent(new TextButton(this, this.x + 25, this.y + 75, I18n.format("sphinx.yes"), () -> {
			NetworkHandler.instance.sendToServer(
					MessageShutdownSphinx.newMessage(mc.player, ConnectHelperClient.getInstance().getNetworkPos()));
			mc.displayGuiScreen(null);
		}));
		addComponent(new TextButton(this, this.x + 120, this.y + 75, I18n.format("sphinx.no"), () -> {
			parent.removeScreen(SubscreenShutdownSphinx.this);
		}));
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Gui.drawRect(x, y, x + width, y + height, 0xAF282828);
		Util.renderSplitGlowString(Util.formatAndAntiEscape("sphinx.shutdown_sphinx"), x + 5, y + 5, 150);
		Util.renderGlowBorder(x, y, width, height);
	}
}
